package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.Technique;
import com.cesde.edupulse.dto.catalog.TechniqueRequest;
import com.cesde.edupulse.dto.catalog.TechniqueResponse;
import com.cesde.edupulse.repository.AcademicGroupRepository;
import com.cesde.edupulse.repository.TechniqueRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TechniqueService {

    private final TechniqueRepository techniqueRepository;
    private final AcademicGroupRepository academicGroupRepository;

    @Transactional(readOnly = true)
    public List<TechniqueResponse> findAll() {
        return techniqueRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TechniqueResponse create(TechniqueRequest request) {
        String normalizedCode = normalizeCode(request.code());
        String normalizedName = normalizeName(request.name());

        validateUniqueness(normalizedCode, normalizedName, null);

        Technique saved = techniqueRepository.save(Objects.requireNonNull(Technique.builder()
                .code(normalizedCode)
                .name(normalizedName)
                .build()));

        return toResponse(saved);
    }

    @Transactional
    public TechniqueResponse update(Long id, TechniqueRequest request) {
        Long techniqueId = Objects.requireNonNull(id);
        Technique technique = techniqueRepository.findById(techniqueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tecnica no encontrada"));

        String normalizedCode = normalizeCode(request.code());
        String normalizedName = normalizeName(request.name());

        validateUniqueness(normalizedCode, normalizedName, techniqueId);

        technique.setCode(normalizedCode);
        technique.setName(normalizedName);

        return toResponse(techniqueRepository.save(technique));
    }

    @Transactional
    public void delete(Long id) {
        Long techniqueId = Objects.requireNonNull(id);
        Technique technique = techniqueRepository.findById(techniqueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tecnica no encontrada"));

        if (academicGroupRepository.existsByTechniqueId(techniqueId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una tecnica asociada a grupos academicos");
        }

        techniqueRepository.delete(Objects.requireNonNull(technique));
    }

    private void validateUniqueness(String code, String name, Long currentId) {
        boolean codeExists = currentId == null
                ? techniqueRepository.existsByCodeIgnoreCase(code)
                : techniqueRepository.existsByCodeIgnoreCaseAndIdNot(code, currentId);
        if (codeExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una tecnica con ese codigo");
        }

        boolean nameExists = currentId == null
                ? techniqueRepository.existsByNameIgnoreCase(name)
                : techniqueRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (nameExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una tecnica con ese nombre");
        }
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private TechniqueResponse toResponse(Technique technique) {
        return new TechniqueResponse(technique.getId(), technique.getCode(), technique.getName());
    }
}