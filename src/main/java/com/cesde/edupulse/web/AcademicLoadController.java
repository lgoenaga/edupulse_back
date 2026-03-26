package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicLoadRequest;
import com.cesde.edupulse.dto.catalog.AcademicLoadResponse;
import com.cesde.edupulse.service.AcademicLoadService;
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
@RequestMapping("/api/admin/loads")
@RequiredArgsConstructor
public class AcademicLoadController {

    private final AcademicLoadService academicLoadService;

    @GetMapping
    public List<AcademicLoadResponse> findAll() {
        return academicLoadService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<AcademicLoadResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return academicLoadService.findPage(page, size);
    }

    @PostMapping
    public AcademicLoadResponse create(@Valid @RequestBody AcademicLoadRequest request) {
        return academicLoadService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicLoadResponse update(@PathVariable Long id, @Valid @RequestBody AcademicLoadRequest request) {
        return academicLoadService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        academicLoadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}