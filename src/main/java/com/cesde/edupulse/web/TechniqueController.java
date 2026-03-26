package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.TechniqueRequest;
import com.cesde.edupulse.dto.catalog.TechniqueResponse;
import com.cesde.edupulse.service.TechniqueService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/techniques")
@RequiredArgsConstructor
public class TechniqueController {

    private final TechniqueService techniqueService;

    @GetMapping
    public List<TechniqueResponse> findAll() {
        return techniqueService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<TechniqueResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return techniqueService.findPage(page, size);
    }

    @PostMapping
    public TechniqueResponse create(@Valid @RequestBody TechniqueRequest request) {
        return techniqueService.create(request);
    }

    @PutMapping("/{id}")
    public TechniqueResponse update(@PathVariable Long id, @Valid @RequestBody TechniqueRequest request) {
        return techniqueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        techniqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}