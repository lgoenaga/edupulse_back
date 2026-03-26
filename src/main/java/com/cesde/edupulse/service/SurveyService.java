package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.enums.EvaluationCategory;
import com.cesde.edupulse.domain.model.AcademicLoad;
import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.domain.model.AcademicSubject;
import com.cesde.edupulse.domain.model.Question;
import com.cesde.edupulse.domain.model.Student;
import com.cesde.edupulse.domain.model.SurveyResponse;
import com.cesde.edupulse.domain.model.SurveySubmission;
import com.cesde.edupulse.domain.model.Teacher;
import com.cesde.edupulse.dto.survey.StudentContextResponse;
import com.cesde.edupulse.dto.survey.SubmitSurveyRequest;
import com.cesde.edupulse.dto.survey.SurveyAnswerRequest;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.AcademicPeriodRepository;
import com.cesde.edupulse.repository.AcademicSubjectRepository;
import com.cesde.edupulse.repository.QuestionRepository;
import com.cesde.edupulse.repository.StudentRepository;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import com.cesde.edupulse.repository.SurveySubmissionRepository;
import com.cesde.edupulse.repository.TeacherRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SurveyService {

        private final StudentRepository studentRepository;
        private final AcademicPeriodRepository academicPeriodRepository;
        private final QuestionRepository questionRepository;
        private final AcademicLoadRepository academicLoadRepository;
        private final SurveySubmissionRepository surveySubmissionRepository;
        private final SurveyResponseRepository surveyResponseRepository;
        private final TeacherRepository teacherRepository;
        private final AcademicSubjectRepository academicSubjectRepository;

        @Transactional(readOnly = true)
        public StudentContextResponse getStudentContext(String username) {
                Student student = getStudent(username);
                AcademicPeriod activePeriod = getActivePeriod();
                Long studentId = Objects.requireNonNull(student.getId());
                Long activePeriodId = Objects.requireNonNull(activePeriod.getId());

                List<Question> questions = questionRepository.findByActiveTrueOrderByDisplayOrderAsc();
                List<AcademicLoad> loads = academicLoadRepository.findByGroupIdAndPeriodIdAndActiveTrue(
                                Objects.requireNonNull(student.getGroup().getId()),
                                activePeriodId);
                SurveySubmission existingSubmission = surveySubmissionRepository
                                .findByStudentIdAndPeriodId(studentId, activePeriodId)
                                .orElse(null);

                return new StudentContextResponse(
                                activePeriodId,
                                activePeriod.getName(),
                                student.getFullName(),
                                student.getGroup().getName(),
                                student.getGroup().getTechnique().getName(),
                                existingSubmission != null,
                                existingSubmission != null ? existingSubmission.getSubmittedAt() : null,
                                questions.stream()
                                                .map(question -> new StudentContextResponse.QuestionItem(
                                                                question.getId(),
                                                                question.getPrompt(),
                                                                question.getCategory().name(),
                                                                question.getDisplayOrder()))
                                                .toList(),
                                loads.stream()
                                                .map(load -> new StudentContextResponse.TeacherAssignmentItem(
                                                                load.getTeacher().getId(),
                                                                load.getTeacher().getFullName(),
                                                                load.getSubject().getId(),
                                                                load.getSubject().getName()))
                                                .toList());
        }

        @Transactional
        public void submitSurvey(String username, SubmitSurveyRequest request) {
                Student student = getStudent(username);
                AcademicPeriod activePeriod = getActivePeriod();
                Long studentId = Objects.requireNonNull(student.getId());
                Long activePeriodId = Objects.requireNonNull(activePeriod.getId());

                surveySubmissionRepository.findByStudentIdAndPeriodId(studentId, activePeriodId)
                                .ifPresent(existing -> {
                                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                                        "El estudiante ya respondió la encuesta del período vigente");
                                });

                List<Long> questionIds = new ArrayList<>(request.responses().stream()
                                .map(answer -> Objects.requireNonNull(answer.questionId()))
                                .toList());

                Map<Long, Question> questionMap = questionRepository.findAllById(questionIds)
                                .stream()
                                .collect(java.util.stream.Collectors.toMap(Question::getId, Function.identity()));

                List<AcademicLoad> loads = academicLoadRepository.findByGroupIdAndPeriodIdAndActiveTrue(
                                Objects.requireNonNull(student.getGroup().getId()),
                                activePeriodId);

                SurveySubmission submissionToSave = Objects.requireNonNull(SurveySubmission.builder()
                                .student(student)
                                .period(activePeriod)
                                .build());

                SurveySubmission submission = Objects.requireNonNull(
                                surveySubmissionRepository.save(Objects.requireNonNull(submissionToSave)));

                List<SurveyResponse> responses = request.responses().stream()
                                .map(answer -> toResponse(answer, submission, questionMap, loads))
                                .toList();

                surveyResponseRepository.saveAll(new ArrayList<>(responses));
        }

        private SurveyResponse toResponse(
                        SurveyAnswerRequest answer,
                        SurveySubmission submission,
                        Map<Long, Question> questionMap,
                        List<AcademicLoad> loads) {
                Question question = questionMap.get(answer.questionId());
                if (question == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pregunta no válida en la encuesta");
                }

                Teacher teacher = null;
                AcademicSubject subject = null;

                if (question.getCategory() == EvaluationCategory.DOCENTE) {
                        if (answer.teacherId() == null || answer.subjectId() == null) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Las preguntas DOCENTE requieren docente y materia");
                        }

                        Long teacherId = Objects.requireNonNull(answer.teacherId());
                        Long subjectId = Objects.requireNonNull(answer.subjectId());

                        boolean validLoad = loads.stream()
                                        .anyMatch(load -> load.getTeacher().getId().equals(teacherId)
                                                        && load.getSubject().getId().equals(subjectId));
                        if (!validLoad) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "La relación docente-materia no pertenece a la carga académica del estudiante");
                        }

                        teacher = teacherRepository.findById(teacherId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                        "Docente no encontrado"));
                        subject = academicSubjectRepository.findById(subjectId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                        "Materia no encontrada"));
                }

                return SurveyResponse.builder()
                                .submission(submission)
                                .question(question)
                                .score(answer.score())
                                .teacher(teacher)
                                .subject(subject)
                                .build();
        }

        private Student getStudent(String username) {
                return studentRepository.findByUserUsername(username)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Estudiante no encontrado"));
        }

        private AcademicPeriod getActivePeriod() {
                return academicPeriodRepository.findByActiveTrue()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "No existe un período activo configurado"));
        }
}