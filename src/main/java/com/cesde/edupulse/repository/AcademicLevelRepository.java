package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicLevelRepository extends JpaRepository<AcademicLevel, Long> {
    List<AcademicLevel> findAllByOrderByDisplayOrderAscNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}