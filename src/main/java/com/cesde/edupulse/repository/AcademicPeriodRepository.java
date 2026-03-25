package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicPeriod;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {
    Optional<AcademicPeriod> findByActiveTrue();
}