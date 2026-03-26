package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.SurveySubmission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {
    Optional<SurveySubmission> findByStudentIdAndPeriodId(Long studentId, Long periodId);

    boolean existsByPeriodId(Long periodId);

    boolean existsByStudentId(Long studentId);

    @Query("""
            select submission
            from SurveySubmission submission
            join fetch submission.student student
            join fetch student.group academicGroup
            join fetch academicGroup.level level
            join fetch academicGroup.technique technique
            join fetch submission.period period
            where (:periodId is null or period.id = :periodId)
              and (:groupId is null or academicGroup.id = :groupId)
              and (:levelId is null or level.id = :levelId)
              and (:studentId is null or student.id = :studentId)
            order by submission.submittedAt desc
            """)
    List<SurveySubmission> findAllForAdmin(
            @Param("periodId") Long periodId,
            @Param("groupId") Long groupId,
            @Param("levelId") Long levelId,
            @Param("studentId") Long studentId);

    @Query("""
            select submission
            from SurveySubmission submission
            join fetch submission.student student
            join fetch student.group academicGroup
            join fetch academicGroup.level level
            join fetch academicGroup.technique technique
            join fetch submission.period period
            where submission.id = :submissionId
            """)
    Optional<SurveySubmission> findForAdminById(@Param("submissionId") Long submissionId);
}