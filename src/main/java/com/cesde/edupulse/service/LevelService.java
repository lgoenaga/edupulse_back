package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.AcademicLevel;
import com.cesde.edupulse.dto.catalog.AcademicLevelRequest;
import com.cesde.edupulse.dto.catalog.AcademicLevelResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.AcademicLevelRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final AcademicLevelRepository academicLevelRepository;
    private final AcademicGroupRepository academicGroupRepository;

    @Transactional(readOnly = true)
    public List<AcademicLevelResponse> findAll() {
        return academicLevelRepository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AcademicLevelResponse create(AcademicLevelRequest request) {
        String normalizedName = normalizeName(request.name());
        validateUniqueness(normalizedName, null);

        AcademicLevel saved = academicLevelRepository.save(Objects.requireNonNull(AcademicLevel.builder()
                .name(normalizedName)
                .displayOrder(request.displayOrder())
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public AcademicLevelResponse update(Long id, AcademicLevelRequest request) {
        Long levelId = Objects.requireNonNull(id);
        AcademicLevel level = academicLevelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel no encontrado"));

        String normalizedName = normalizeName(request.name());
        validateUniqueness(normalizedName, levelId);

        level.setName(normalizedName);
        level.setDisplayOrder(request.displayOrder());

        return toResponse(academicLevelRepository.save(level));
    }

    @Transactional
    public void delete(Long id) {
        Long levelId = Objects.requireNonNull(id);
        AcademicLevel level = academicLevelRepository.findById(levelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel no encontrado"));

        if (academicGroupRepository.existsByLevelId(levelId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un nivel asociado a grupos academicos");
        }

        academicLevelRepository.delete(Objects.requireNonNull(level));
    }

    private void validateUniqueness(String name, Long currentId) {
        boolean nameExists = currentId == null
                ? academicLevelRepository.existsByNameIgnoreCase(name)
                : academicLevelRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (nameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un nivel con ese nombre");
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private AcademicLevelResponse toResponse(AcademicLevel level) {
        return new AcademicLevelResponse(level.getId(), level.getName(), level.getDisplayOrder());
    }
}