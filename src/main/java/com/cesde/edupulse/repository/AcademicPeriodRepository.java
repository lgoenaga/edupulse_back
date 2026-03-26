package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicPeriod;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {
    List<AcademicPeriod> findAllByOrderByYearDescTermNumberDesc();

    Page<AcademicPeriod> findAllByOrderByYearDescTermNumberDesc(Pageable pageable);

    Optional<AcademicPeriod> findByActiveTrue();
}