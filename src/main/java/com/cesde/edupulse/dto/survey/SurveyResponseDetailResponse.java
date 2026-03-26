package com.cesde.edupulse.dto.survey;

public record SurveyResponseDetailResponse(
        Long questionId,
        String prompt,
        String category,
        Integer score,
        String teacherName,
        String subjectName) {
}
