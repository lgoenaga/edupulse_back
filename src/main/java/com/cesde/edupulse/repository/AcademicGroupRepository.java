package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicGroupRepository extends JpaRepository<AcademicGroup, Long> {
    List<AcademicGroup> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByLevelId(Long levelId);

    boolean existsByTechniqueId(Long techniqueId);
}