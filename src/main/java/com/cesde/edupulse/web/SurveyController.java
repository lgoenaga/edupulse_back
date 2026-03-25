package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.survey.StudentContextResponse;
import com.cesde.edupulse.dto.survey.SubmitSurveyRequest;
import com.cesde.edupulse.service.SurveyService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping("/context")
    public StudentContextResponse getContext(Principal principal) {
        return surveyService.getStudentContext(principal.getName());
    }

    @PostMapping("/surveys")
    public ResponseEntity<Void> submitSurvey(Principal principal, @Valid @RequestBody SubmitSurveyRequest request) {
        surveyService.submitSurvey(principal.getName(), request);
        return ResponseEntity.noContent().build();
    }
}