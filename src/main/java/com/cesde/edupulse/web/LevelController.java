package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.catalog.AcademicLevelRequest;
import com.cesde.edupulse.dto.catalog.AcademicLevelResponse;
import com.cesde.edupulse.service.LevelService;
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
@RequestMapping("/api/admin/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping
    public List<AcademicLevelResponse> findAll() {
        return levelService.findAll();
    }

    @PostMapping
    public AcademicLevelResponse create(@Valid @RequestBody AcademicLevelRequest request) {
        return levelService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicLevelResponse update(@PathVariable Long id, @Valid @RequestBody AcademicLevelRequest request) {
        return levelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        levelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}