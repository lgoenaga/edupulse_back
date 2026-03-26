package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicLevel;
import com.cesde.edupulse.domain.model.AcademicSubject;
import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicSubjectRequest;
import com.cesde.edupulse.dto.catalog.AcademicSubjectResponse;
import com.cesde.edupulse.repository.AcademicLevelRepository;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.AcademicSubjectRepository;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final AcademicSubjectRepository academicSubjectRepository;
    private final AcademicLevelRepository academicLevelRepository;
    private final AcademicLoadRepository academicLoadRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public List<AcademicSubjectResponse> findAll() {
        return academicSubjectRepository.findAllByOrderByLevelDisplayOrderAscNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AcademicSubjectResponse> findPage(int page, int size) {
        validatePagination(page, size);

        Page<AcademicSubject> subjectPage = academicSubjectRepository.findAllByOrderByLevelDisplayOrderAscNameAsc(
                PageRequest.of(page, size));
        List<AcademicSubjectResponse> items = subjectPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                subjectPage.getNumber(),
                subjectPage.getSize(),
                subjectPage.getTotalElements(),
                subjectPage.getTotalPages(),
                subjectPage.isFirst(),
                subjectPage.isLast());
    }

    @Transactional
    public AcademicSubjectResponse create(AcademicSubjectRequest request) {
        String normalizedCode = normalizeCode(request.code());
        String normalizedName = normalizeName(request.name());
        Long levelId = Objects.requireNonNull(request.levelId());

        validateUniqueness(normalizedCode, normalizedName, levelId, null);
        AcademicLevel level = findLevel(levelId);

        AcademicSubject saved = academicSubjectRepository.save(Objects.requireNonNull(AcademicSubject.builder()
                .code(normalizedCode)
                .name(normalizedName)
                .level(level)
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public AcademicSubjectResponse update(Long id, AcademicSubjectRequest request) {
        Long subjectId = Objects.requireNonNull(id);
        AcademicSubject subject = academicSubjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

        String normalizedCode = normalizeCode(request.code());
        String normalizedName = normalizeName(request.name());
        Long levelId = Objects.requireNonNull(request.levelId());

        validateUniqueness(normalizedCode, normalizedName, levelId, subjectId);

        subject.setCode(normalizedCode);
        subject.setName(normalizedName);
        subject.setLevel(findLevel(levelId));

        return toResponse(academicSubjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        Long subjectId = Objects.requireNonNull(id);
        AcademicSubject subject = academicSubjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

        if (academicLoadRepository.existsBySubjectId(subjectId)
                || surveyResponseRepository.existsBySubjectId(subjectId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una materia asociada a carga academica o respuestas de encuesta");
        }

        academicSubjectRepository.delete(Objects.requireNonNull(subject));
    }

    private void validateUniqueness(String code, String name, Long levelId, Long currentId) {
        boolean codeExists = currentId == null
                ? academicSubjectRepository.existsByCodeIgnoreCase(code)
                : academicSubjectRepository.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        if (codeExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una materia con ese codigo");
        }

        boolean nameExists = currentId == null
                ? academicSubjectRepository.existsByNameIgnoreCaseAndLevelId(name, levelId)
                : academicSubjectRepository.existsByNameIgnoreCaseAndLevelIdAndIdNot(name, levelId, currentId);
        if (nameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una materia con ese nombre en el nivel seleccionado");
        }
    }

    private AcademicLevel findLevel(Long levelId) {
        return academicLevelRepository.findById(Objects.requireNonNull(levelId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel no encontrado"));
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
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

    private AcademicSubjectResponse toResponse(AcademicSubject subject) {
        return new AcademicSubjectResponse(
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getLevel().getId(),
                subject.getLevel().getName());
    }
}