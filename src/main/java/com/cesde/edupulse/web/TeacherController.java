package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.TeacherRequest;
import com.cesde.edupulse.dto.catalog.TeacherResponse;
import com.cesde.edupulse.service.TeacherService;
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
@RequestMapping("/api/admin/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public List<TeacherResponse> findAll() {
        return teacherService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<TeacherResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return teacherService.findPage(page, size);
    }

    @PostMapping
    public TeacherResponse create(@Valid @RequestBody TeacherRequest request) {
        return teacherService.create(request);
    }

    @PutMapping("/{id}")
    public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return teacherService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}