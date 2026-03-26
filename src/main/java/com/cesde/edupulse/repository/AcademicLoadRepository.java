package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicLoad;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

public interface AcademicLoadRepository extends JpaRepository<AcademicLoad, Long> {
        List<AcademicLoad> findByGroupIdAndPeriodIdAndActiveTrue(Long groupId, Long periodId);

        @EntityGraph(attributePaths = {
                        "teacher",
                        "subject",
                        "group",
                        "period"
        })
        @Query("select load from AcademicLoad load")
        Page<AcademicLoad> findPageForAdmin(Pageable pageable);

        boolean existsByGroupId(Long groupId);

        boolean existsBySubjectId(Long subjectId);

        boolean existsByTeacherId(Long teacherId);

        boolean existsByTeacherIdAndSubjectIdAndGroupIdAndPeriodId(
                        Long teacherId,
                        Long subjectId,
                        Long groupId,
                        Long periodId);

        boolean existsByTeacherIdAndSubjectIdAndGroupIdAndPeriodIdAndIdNot(
                        Long teacherId,
                        Long subjectId,
                        Long groupId,
                        Long periodId,
                        Long id);
}