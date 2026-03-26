package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.SurveySubmission;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveySubmissionRepository extends JpaRepository<SurveySubmission, Long> {
    Optional<SurveySubmission> findByStudentIdAndPeriodId(Long studentId, Long periodId);

    boolean existsByPeriodId(Long periodId);

    boolean existsByStudentId(Long studentId);

    @EntityGraph(attributePaths = {
            "student",
            "student.group",
            "student.group.level",
            "student.group.technique",
            "period"
    })
    @Query(value = """
            select submission
            from SurveySubmission submission
            join submission.student student
            join student.group academicGroup
            join academicGroup.level level
            join academicGroup.technique technique
            join submission.period period
            where (:periodId is null or period.id = :periodId)
              and (:groupId is null or academicGroup.id = :groupId)
              and (:levelId is null or level.id = :levelId)
              and (:studentId is null or student.id = :studentId)
                                                        and (:applySubmittedFrom = false or submission.submittedAt >= :submittedFrom)
                                                        and (:applySubmittedTo = false or submission.submittedAt < :submittedTo)
            """, countQuery = """
            select count(submission)
            from SurveySubmission submission
            join submission.student student
            join student.group academicGroup
            join academicGroup.level level
            join submission.period period
            where (:periodId is null or period.id = :periodId)
              and (:groupId is null or academicGroup.id = :groupId)
              and (:levelId is null or level.id = :levelId)
              and (:studentId is null or student.id = :studentId)
                                                        and (:applySubmittedFrom = false or submission.submittedAt >= :submittedFrom)
                                                        and (:applySubmittedTo = false or submission.submittedAt < :submittedTo)
            """)
    Page<SurveySubmission> findAllForAdmin(
            @Param("periodId") Long periodId,
            @Param("groupId") Long groupId,
            @Param("levelId") Long levelId,
            @Param("studentId") Long studentId,
            @Param("applySubmittedFrom") boolean applySubmittedFrom,
            @Param("submittedFrom") OffsetDateTime submittedFrom,
            @Param("applySubmittedTo") boolean applySubmittedTo,
            @Param("submittedTo") OffsetDateTime submittedTo,
            Pageable pageable);

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