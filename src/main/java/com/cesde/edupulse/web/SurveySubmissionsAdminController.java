package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.survey.SurveySubmissionDetailResponse;
import com.cesde.edupulse.dto.survey.SurveySubmissionSummaryResponse;
import com.cesde.edupulse.service.SurveySubmissionsAdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/survey-submissions")
@RequiredArgsConstructor
public class SurveySubmissionsAdminController {

    private final SurveySubmissionsAdminService surveySubmissionsAdminService;

    @GetMapping
    public List<SurveySubmissionSummaryResponse> listSubmissions(
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long studentId) {
        return surveySubmissionsAdminService.listSubmissions(periodId, groupId, levelId, studentId);
    }

    @GetMapping("/{submissionId}")
    public SurveySubmissionDetailResponse getSubmissionDetail(@PathVariable Long submissionId) {
        return surveySubmissionsAdminService.getSubmissionDetail(submissionId);
    }
}
