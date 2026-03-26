package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicGroup;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface AcademicGroupRepository extends JpaRepository<AcademicGroup, Long> {
    List<AcademicGroup> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = { "level", "technique" })
    Page<AcademicGroup> findAllByOrderByNameAsc(Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByLevelId(Long levelId);

    boolean existsByTechniqueId(Long techniqueId);
}