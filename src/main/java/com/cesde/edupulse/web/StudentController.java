package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.StudentRequest;
import com.cesde.edupulse.dto.catalog.StudentResponse;
import com.cesde.edupulse.service.StudentAdminService;
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
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentAdminService studentAdminService;

    @GetMapping
    public List<StudentResponse> findAll() {
        return studentAdminService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<StudentResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return studentAdminService.findPage(page, size);
    }

    @PostMapping
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentAdminService.create(request);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return studentAdminService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}