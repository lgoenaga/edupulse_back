package com.cesde.edupulse.dto.survey;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SubmitSurveyRequest(
        @Valid @NotEmpty List<SurveyAnswerRequest> responses
) {
}