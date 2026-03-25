package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.AcademicLoad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicLoadRepository extends JpaRepository<AcademicLoad, Long> {
    List<AcademicLoad> findByGroupIdAndPeriodIdAndActiveTrue(Long groupId, Long periodId);

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