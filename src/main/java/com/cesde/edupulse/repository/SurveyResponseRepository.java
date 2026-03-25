package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    boolean existsBySubjectId(Long subjectId);

    boolean existsByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndSubjectIdAndSubmissionStudentGroupIdAndSubmissionPeriodId(
            Long teacherId,
            Long subjectId,
            Long groupId,
            Long periodId);
}