package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicPeriodRequest;
import com.cesde.edupulse.dto.catalog.AcademicPeriodResponse;
import com.cesde.edupulse.service.PeriodService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/admin/periods")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodService periodService;

    @GetMapping
    public List<AcademicPeriodResponse> findAll() {
        return periodService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<AcademicPeriodResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return periodService.findPage(page, size);
    }

    @PostMapping
    public AcademicPeriodResponse create(@Valid @RequestBody AcademicPeriodRequest request) {
        return periodService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicPeriodResponse update(@PathVariable Long id, @Valid @RequestBody AcademicPeriodRequest request) {
        return periodService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        periodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}