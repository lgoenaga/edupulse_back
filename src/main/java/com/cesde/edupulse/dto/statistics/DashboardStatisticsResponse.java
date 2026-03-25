package com.cesde.edupulse.dto.statistics;

import java.util.List;

public record DashboardStatisticsResponse(
        long totalResponses,
        List<AggregateAverageResponse> categoryAverages,
        List<AggregateAverageResponse> techniqueAverages,
        List<AggregateAverageResponse> teacherAverages
) {
}