package com.cesde.edupulse.dto.survey;

import java.time.OffsetDateTime;
import java.util.List;

public record StudentContextResponse(
        Long activePeriodId,
        String activePeriodName,
        String studentName,
        String groupName,
        String techniqueName,
        boolean hasSubmitted,
        OffsetDateTime submittedAt,
        List<QuestionItem> questions,
        List<TeacherAssignmentItem> teacherAssignments) {
    public record QuestionItem(Long id, String prompt, String category, Integer displayOrder) {
    }

    public record TeacherAssignmentItem(Long teacherId, String teacherName, Long subjectId, String subjectName) {
    }
}