package com.cesde.edupulse.dto.survey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SurveyAnswerRequest(
        @NotNull Long questionId,
        @NotNull @Min(1) @Max(5) Integer score,
        Long teacherId,
        Long subjectId
) {
}