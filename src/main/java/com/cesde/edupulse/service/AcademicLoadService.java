package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicGroup;
import com.cesde.edupulse.domain.model.AcademicLoad;
import com.cesde.edupulse.domain.model.AcademicPeriod;
import com.cesde.edupulse.domain.model.AcademicSubject;
import com.cesde.edupulse.domain.model.Teacher;
import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicLoadRequest;
import com.cesde.edupulse.dto.catalog.AcademicLoadResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.AcademicPeriodRepository;
import com.cesde.edupulse.repository.AcademicSubjectRepository;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import com.cesde.edupulse.repository.TeacherRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
                Sort loadSort = Objects.requireNonNull(buildLoadSort());
                return academicLoadRepository.findAll(loadSort).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public PageResponse<AcademicLoadResponse> findPage(int page, int size) {
                validatePagination(page, size);
                Sort loadSort = Objects.requireNonNull(buildLoadSort());

                Page<AcademicLoad> loadPage = academicLoadRepository.findPageForAdmin(
                                PageRequest.of(page, size, loadSort));
                List<AcademicLoadResponse> items = loadPage.getContent().stream()
                                .map(this::toResponse)
                                .toList();

                return new PageResponse<>(
                                items,
                                loadPage.getNumber(),
                                loadPage.getSize(),
                                loadPage.getTotalElements(),
                                loadPage.getTotalPages(),
                                loadPage.isFirst(),
                                loadPage.isLast());
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
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Carga academica no encontrada"));

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
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Carga academica no encontrada"));

                if (surveyResponseRepository
                                .existsByTeacherIdAndSubjectIdAndSubmissionStudentGroupIdAndSubmissionPeriodId(
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
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Docente no encontrado"));
        }

        private AcademicSubject findSubject(Long subjectId) {
                return academicSubjectRepository.findById(Objects.requireNonNull(subjectId))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Materia no encontrada"));
        }

        private AcademicGroup findGroup(Long groupId) {
                return academicGroupRepository.findById(Objects.requireNonNull(groupId))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Grupo no encontrado"));
        }

        private AcademicPeriod findPeriod(Long periodId) {
                return academicPeriodRepository.findById(Objects.requireNonNull(periodId))
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Periodo no encontrado"));
        }

        private void validatePagination(int page, int size) {
                if (page < 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La pagina no puede ser negativa");
                }

                if (size < 1 || size > 100) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "El tamano de pagina debe estar entre 1 y 100");
                }
        }

        private Sort buildLoadSort() {
                return Sort.by(
                                Sort.Order.desc("period.year"),
                                Sort.Order.desc("period.termNumber"),
                                Sort.Order.asc("group.name"),
                                Sort.Order.asc("subject.name"),
                                Sort.Order.asc("teacher.firstName"),
                                Sort.Order.asc("teacher.lastName"));
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