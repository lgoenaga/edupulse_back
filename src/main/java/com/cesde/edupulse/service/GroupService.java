package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicGroup;
import com.cesde.edupulse.domain.model.AcademicLevel;
import com.cesde.edupulse.domain.model.Technique;
import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicGroupRequest;
import com.cesde.edupulse.dto.catalog.AcademicGroupResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AcademicLevelRepository;
import com.cesde.edupulse.repository.AcademicLoadRepository;
import com.cesde.edupulse.repository.StudentRepository;
import com.cesde.edupulse.repository.TechniqueRepository;
import java.util.List;
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
public class GroupService {

    private final AcademicGroupRepository academicGroupRepository;
    private final AcademicLevelRepository academicLevelRepository;
    private final TechniqueRepository techniqueRepository;
    private final StudentRepository studentRepository;
    private final AcademicLoadRepository academicLoadRepository;

    @Transactional(readOnly = true)
    public List<AcademicGroupResponse> findAll() {
        return academicGroupRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AcademicGroupResponse> findPage(int page, int size) {
        validatePagination(page, size);

        Page<AcademicGroup> groupPage = academicGroupRepository.findAllByOrderByNameAsc(PageRequest.of(page, size));
        List<AcademicGroupResponse> items = groupPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                groupPage.getNumber(),
                groupPage.getSize(),
                groupPage.getTotalElements(),
                groupPage.getTotalPages(),
                groupPage.isFirst(),
                groupPage.isLast());
    }

    @Transactional
    public AcademicGroupResponse create(AcademicGroupRequest request) {
        String normalizedName = normalizeName(request.name());
        validateUniqueness(normalizedName, null);

        AcademicLevel level = findLevel(request.levelId());
        Technique technique = findTechnique(request.techniqueId());

        AcademicGroup saved = academicGroupRepository.save(Objects.requireNonNull(AcademicGroup.builder()
                .name(normalizedName)
                .level(level)
                .technique(technique)
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public AcademicGroupResponse update(Long id, AcademicGroupRequest request) {
        Long groupId = Objects.requireNonNull(id);
        AcademicGroup group = academicGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));

        String normalizedName = normalizeName(request.name());
        validateUniqueness(normalizedName, groupId);

        group.setName(normalizedName);
        group.setLevel(findLevel(request.levelId()));
        group.setTechnique(findTechnique(request.techniqueId()));

        return toResponse(academicGroupRepository.save(group));
    }

    @Transactional
    public void delete(Long id) {
        Long groupId = Objects.requireNonNull(id);
        AcademicGroup group = academicGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));

        if (studentRepository.existsByGroupId(groupId) || academicLoadRepository.existsByGroupId(groupId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un grupo asociado a estudiantes o carga academica");
        }

        academicGroupRepository.delete(Objects.requireNonNull(group));
    }

    private void validateUniqueness(String name, Long currentId) {
        boolean nameExists = currentId == null
                ? academicGroupRepository.existsByNameIgnoreCase(name)
                : academicGroupRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);

        if (nameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un grupo con ese nombre");
        }
    }

    private AcademicLevel findLevel(Long levelId) {
        return academicLevelRepository.findById(Objects.requireNonNull(levelId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel no encontrado"));
    }

    private Technique findTechnique(Long techniqueId) {
        return techniqueRepository.findById(Objects.requireNonNull(techniqueId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tecnica no encontrada"));
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

    private AcademicGroupResponse toResponse(AcademicGroup group) {
        return new AcademicGroupResponse(
                group.getId(),
                group.getName(),
                group.getLevel().getId(),
                group.getLevel().getName(),
                group.getTechnique().getId(),
                group.getTechnique().getName());
    }
}