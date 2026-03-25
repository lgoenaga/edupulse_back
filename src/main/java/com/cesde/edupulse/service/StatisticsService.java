package com.cesde.edupulse.service;

import com.cesde.edupulse.domain.model.SurveyResponse;
import com.cesde.edupulse.dto.statistics.AggregateAverageResponse;
import com.cesde.edupulse.dto.statistics.DashboardStatisticsResponse;
import com.cesde.edupulse.repository.SurveyResponseRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SurveyResponseRepository surveyResponseRepository;

    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics(
            Long levelId,
            Long groupId,
            Long periodId,
            Long techniqueId,
            Long teacherId) {
        List<SurveyResponse> filtered = surveyResponseRepository.findAll().stream()
                .filter(response -> periodId == null || response.getSubmission().getPeriod().getId().equals(periodId))
                .filter(response -> levelId == null
                        || response.getSubmission().getStudent().getGroup().getLevel().getId().equals(levelId))
                .filter(response -> groupId == null
                        || response.getSubmission().getStudent().getGroup().getId().equals(groupId))
                .filter(response -> techniqueId == null
                        || response.getSubmission().getStudent().getGroup().getTechnique().getId().equals(techniqueId))
                .filter(response -> teacherId == null
                        || (response.getTeacher() != null && response.getTeacher().getId().equals(teacherId)))
                .toList();

        return new DashboardStatisticsResponse(
                filtered.size(),
                aggregate(filtered, response -> response.getQuestion().getCategory().name(), response -> null),
                aggregate(filtered,
                        response -> response.getSubmission().getStudent().getGroup().getTechnique().getName(),
                        response -> response.getSubmission().getStudent().getGroup().getTechnique().getId()),
                aggregate(filtered.stream().filter(response -> response.getTeacher() != null).toList(),
                        response -> response.getTeacher().getFullName(),
                        response -> response.getTeacher().getId()));
    }

    private List<AggregateAverageResponse> aggregate(
            List<SurveyResponse> responses,
            Function<SurveyResponse, String> labelFn,
            Function<SurveyResponse, Long> idFn) {
        Map<String, List<SurveyResponse>> groups = responses.stream().collect(Collectors.groupingBy(labelFn));
        return groups.entrySet().stream()
                .map(entry -> new AggregateAverageResponse(
                        entry.getValue().stream().map(idFn).filter(Objects::nonNull).findFirst().orElse(null),
                        entry.getKey(),
                        round(entry.getValue().stream().mapToInt(SurveyResponse::getScore).average().orElse(0.0)),
                        entry.getValue().size()))
                .sorted(Comparator.comparing(AggregateAverageResponse::label))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}