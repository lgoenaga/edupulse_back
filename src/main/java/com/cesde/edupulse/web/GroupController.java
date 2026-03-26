package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.common.PageResponse;
import com.cesde.edupulse.dto.catalog.AcademicGroupRequest;
import com.cesde.edupulse.dto.catalog.AcademicGroupResponse;
import com.cesde.edupulse.service.GroupService;
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
@RequestMapping("/api/admin/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<AcademicGroupResponse> findAll() {
        return groupService.findAll();
    }

    @GetMapping("/paged")
    public PageResponse<AcademicGroupResponse> findPage(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size) {
        return groupService.findPage(page, size);
    }

    @PostMapping
    public AcademicGroupResponse create(@Valid @RequestBody AcademicGroupRequest request) {
        return groupService.create(request);
    }

    @PutMapping("/{id}")
    public AcademicGroupResponse update(@PathVariable Long id, @Valid @RequestBody AcademicGroupRequest request) {
        return groupService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}