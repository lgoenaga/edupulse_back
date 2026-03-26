package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.SurveyResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    boolean existsBySubjectId(Long subjectId);

    boolean existsByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndSubjectIdAndSubmissionStudentGroupIdAndSubmissionPeriodId(
            Long teacherId,
            Long subjectId,
            Long groupId,
            Long periodId);

    @Query("""
            select response
            from SurveyResponse response
            join fetch response.question question
            left join fetch response.teacher teacher
            left join fetch response.subject subject
            where response.submission.id = :submissionId
            order by question.displayOrder asc, response.id asc
            """)
    List<SurveyResponse> findDetailedBySubmissionId(@Param("submissionId") Long submissionId);

    @Query("""
            select response.submission.id, count(response.id)
            from SurveyResponse response
            where response.submission.id in :submissionIds
            group by response.submission.id
            """)
    List<Object[]> countBySubmissionIds(@Param("submissionIds") List<Long> submissionIds);
}