package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.catalog.AcademicSubjectRequest;
import com.cesde.edupulse.dto.catalog.AcademicSubjectResponse;
import com.cesde.edupulse.service.SubjectService;
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
@RequestMapping("/api/admin/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public List<AcademicSubjectResponse> findAll() {
        return subjectService.findAll();
    }

    @PostMapping
    public AcademicSubjectResponse create(@Valid @RequestBody AcademicSubjectRequest request) {
        return subjectService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicSubjectResponse update(@PathVariable Long id, @Valid @RequestBody AcademicSubjectRequest request) {
        return subjectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}