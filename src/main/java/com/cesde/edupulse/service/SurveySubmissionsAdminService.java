package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.Student;
import com.cesde.edupulse.domain.model.SurveyResponse;
import com.cesde.edupulse.domain.model.SurveySubmission;
import com.cesde.edupulse.dto.survey.SurveyResponseDetailResponse;
import com.cesde.edupulse.dto.survey.SurveySubmissionDetailResponse;
import com.cesde.edupulse.dto.survey.SurveySubmissionSummaryResponse;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import com.cesde.edupulse.repository.SurveySubmissionRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SurveySubmissionsAdminService {

    private final SurveySubmissionRepository surveySubmissionRepository;
    private final SurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public List<SurveySubmissionSummaryResponse> listSubmissions(Long periodId, Long groupId, Long levelId,
            Long studentId, LocalDate submittedFromDate, LocalDate submittedToDate) {
        validateDateRange(submittedFromDate, submittedToDate);

        List<SurveySubmission> submissions = surveySubmissionRepository.findAllForAdmin(
                periodId,
                groupId,
                levelId,
                studentId,
                toRangeStart(submittedFromDate),
                toRangeEndExclusive(submittedToDate));
        Map<Long, Long> responseCounts = getResponseCounts(submissions);

        return submissions.stream()
                .map(submission -> toSummary(submission, responseCounts.getOrDefault(submission.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public SurveySubmissionDetailResponse getSubmissionDetail(Long submissionId) {
        SurveySubmission submission = surveySubmissionRepository.findForAdminById(submissionId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio de encuesta no encontrado"));
        List<SurveyResponseDetailResponse> responses = surveyResponseRepository.findDetailedBySubmissionId(submissionId)
                .stream()
                .map(this::toResponseDetail)
                .toList();

        Student student = submission.getStudent();
        return new SurveySubmissionDetailResponse(
                submission.getId(),
                student.getId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getEmail(),
                student.getGroup().getName(),
                student.getGroup().getLevel().getName(),
                student.getGroup().getTechnique().getName(),
                submission.getPeriod().getName(),
                submission.getSubmittedAt(),
                responses);
    }

    private Map<Long, Long> getResponseCounts(List<SurveySubmission> submissions) {
        List<Long> submissionIds = submissions.stream()
                .map(SurveySubmission::getId)
                .filter(Objects::nonNull)
                .toList();

        if (submissionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return surveyResponseRepository.countBySubmissionIds(submissionIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]));
    }

    private SurveySubmissionSummaryResponse toSummary(SurveySubmission submission, long responseCount) {
        Student student = submission.getStudent();
        return new SurveySubmissionSummaryResponse(
                submission.getId(),
                student.getId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getEmail(),
                student.getGroup().getId(),
                student.getGroup().getName(),
                student.getGroup().getLevel().getId(),
                student.getGroup().getLevel().getName(),
                submission.getPeriod().getId(),
                submission.getPeriod().getName(),
                submission.getSubmittedAt(),
                responseCount);
    }

    private SurveyResponseDetailResponse toResponseDetail(SurveyResponse response) {
        return new SurveyResponseDetailResponse(
                response.getQuestion().getId(),
                response.getQuestion().getPrompt(),
                response.getQuestion().getCategory().name(),
                response.getScore(),
                response.getTeacher() != null ? response.getTeacher().getFullName() : null,
                response.getSubject() != null ? response.getSubject().getName() : null);
    }

    private void validateDateRange(LocalDate submittedFromDate, LocalDate submittedToDate) {
        if (submittedFromDate != null && submittedToDate != null && submittedFromDate.isAfter(submittedToDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser posterior a la fecha final");
        }
    }

    private OffsetDateTime toRangeStart(LocalDate submittedFromDate) {
        if (submittedFromDate == null) {
            return null;
        }

        return submittedFromDate.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private OffsetDateTime toRangeEndExclusive(LocalDate submittedToDate) {
        if (submittedToDate == null) {
            return null;
        }

        return submittedToDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
