package com.cesde.edupulse.dto.survey;

import java.time.OffsetDateTime;
import java.util.List;

public record SurveySubmissionDetailResponse(
        Long id,
        Long studentId,
        String studentCode,
        String studentName,
        String studentEmail,
        String groupName,
        String levelName,
        String techniqueName,
        String periodName,
        OffsetDateTime submittedAt,
        List<SurveyResponseDetailResponse> responses) {
}
