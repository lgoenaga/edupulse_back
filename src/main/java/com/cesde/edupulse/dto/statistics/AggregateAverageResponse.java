package com.cesde.edupulse.dto.statistics;

public record AggregateAverageResponse(
        Long id,
        String label,
        double average,
        long totalResponses
) {
}