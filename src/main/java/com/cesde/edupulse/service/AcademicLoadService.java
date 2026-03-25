package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicGroup;
import com.cesde.edupulse.domain.model.AcademicLoad;
import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.domain.model.AcademicSubject;
import com.cesde.edupulse.domain.model.Teacher;
import com.cesde.edupulse.dto.catalog.AcademicLoadRequest;
import com.cesde.edupulse.dto.catalog.AcademicLoadResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.AcademicPeriodRepository;
import com.cesde.edupulse.repository.AcademicSubjectRepository;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import com.cesde.edupulse.repository.TeacherRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AcademicLoadService {

    private final AcademicLoadRepository academicLoadRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicSubjectRepository academicSubjectRepository;
    private final AcademicGroupRepository academicGroupRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public List<AcademicLoadResponse> findAll() {
        return academicLoadRepository.findAll().stream()
                .sorted(Comparator.comparing((AcademicLoad load) -> load.getPeriod().getYear()).reversed()
                        .thenComparing(load -> load.getPeriod().getTermNumber(), Comparator.reverseOrder())
                        .thenComparing(load -> load.getGroup().getName().toLowerCase())
                        .thenComparing(load -> load.getSubject().getName().toLowerCase())
                        .thenComparing(load -> load.getTeacher().getFullName().toLowerCase()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AcademicLoadResponse create(AcademicLoadRequest request) {
        Teacher teacher = findTeacher(request.teacherId());
        AcademicSubject subject = findSubject(request.subjectId());
        AcademicGroup group = findGroup(request.groupId());
        AcademicPeriod period = findPeriod(request.periodId());

        validateUniqueness(teacher.getId(), subject.getId(), group.getId(), period.getId(), null);

        AcademicLoad saved = academicLoadRepository.save(Objects.requireNonNull(AcademicLoad.builder()
                .teacher(teacher)
                .subject(subject)
                .group(group)
                .period(period)
                .active(request.active())
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public AcademicLoadResponse update(Long id, AcademicLoadRequest request) {
        Long loadId = Objects.requireNonNull(id);
        AcademicLoad load = academicLoadRepository.findById(loadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carga academica no encontrada"));

        Teacher teacher = findTeacher(request.teacherId());
        AcademicSubject subject = findSubject(request.subjectId());
        AcademicGroup group = findGroup(request.groupId());
        AcademicPeriod period = findPeriod(request.periodId());

        validateUniqueness(teacher.getId(), subject.getId(), group.getId(), period.getId(), loadId);

        load.setTeacher(teacher);
        load.setSubject(subject);
        load.setGroup(group);
        load.setPeriod(period);
        load.setActive(request.active());

        return toResponse(academicLoadRepository.save(load));
    }

    @Transactional
    public void delete(Long id) {
        Long loadId = Objects.requireNonNull(id);
        AcademicLoad load = academicLoadRepository.findById(loadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carga academica no encontrada"));

        if (surveyResponseRepository.existsByTeacherIdAndSubjectIdAndSubmissionStudentGroupIdAndSubmissionPeriodId(
                load.getTeacher().getId(),
                load.getSubject().getId(),
                load.getGroup().getId(),
                load.getPeriod().getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una carga academica con respuestas de encuesta asociadas");
        }

        academicLoadRepository.delete(load);
    }

    private void validateUniqueness(Long teacherId, Long subjectId, Long groupId, Long periodId, Long currentId) {
        boolean exists = currentId == null
                ? academicLoadRepository.existsByTeacherIdAndSubjectIdAndGroupIdAndPeriodId(
                        teacherId, subjectId, groupId, periodId)
                : academicLoadRepository.existsByTeacherIdAndSubjectIdAndGroupIdAndPeriodIdAndIdNot(
                        teacherId, subjectId, groupId, periodId, currentId);
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una carga academica con la misma combinacion de docente, materia, grupo y periodo");
        }
    }

    private Teacher findTeacher(Long teacherId) {
        return teacherRepository.findById(Objects.requireNonNull(teacherId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));
    }

    private AcademicSubject findSubject(Long subjectId) {
        return academicSubjectRepository.findById(Objects.requireNonNull(subjectId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));
    }

    private AcademicGroup findGroup(Long groupId) {
        return academicGroupRepository.findById(Objects.requireNonNull(groupId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
    }

    private AcademicPeriod findPeriod(Long periodId) {
        return academicPeriodRepository.findById(Objects.requireNonNull(periodId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo no encontrado"));
    }

    private AcademicLoadResponse toResponse(AcademicLoad load) {
        return new AcademicLoadResponse(
                load.getId(),
                load.getTeacher().getId(),
                load.getTeacher().getFullName(),
                load.getSubject().getId(),
                load.getSubject().getName(),
                load.getGroup().getId(),
                load.getGroup().getName(),
                load.getPeriod().getId(),
                load.getPeriod().getName(),
                load.isActive());
    }
}