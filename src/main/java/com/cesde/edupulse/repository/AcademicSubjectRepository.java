package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicSubject;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface AcademicSubjectRepository extends JpaRepository<AcademicSubject, Long> {
    List<AcademicSubject> findAllByOrderByLevelDisplayOrderAscNameAsc();

    @EntityGraph(attributePaths = { "level" })
    Page<AcademicSubject> findAllByOrderByLevelDisplayOrderAscNameAsc(Pageable pageable);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByNameIgnoreCaseAndLevelId(String name, Long levelId);

    boolean existsByNameIgnoreCaseAndLevelIdAndIdNot(String name, Long levelId, Long id);
}