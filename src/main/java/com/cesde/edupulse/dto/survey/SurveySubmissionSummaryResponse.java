package com.cesde.edupulse.dto.survey;

import java.time.OffsetDateTime;

public record SurveySubmissionSummaryResponse(
        Long id,
        Long studentId,
        String studentCode,
        String studentName,
        String studentEmail,
        Long groupId,
        String groupName,
        Long levelId,
        String levelName,
        Long periodId,
        String periodName,
        OffsetDateTime submittedAt,
        long responseCount) {
}
