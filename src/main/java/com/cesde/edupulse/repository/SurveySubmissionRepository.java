package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.SurveySubmission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {
    Optional<SurveySubmission> findByStudentIdAndPeriodId(Long studentId, Long periodId);

    boolean existsByPeriodId(Long periodId);

    boolean existsByStudentId(Long studentId);
}