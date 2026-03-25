package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.statistics.DashboardStatisticsResponse;
import com.cesde.edupulse.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public DashboardStatisticsResponse getStatistics(
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) Long techniqueId,
            @RequestParam(required = false) Long teacherId) {
        return statisticsService.getDashboardStatistics(levelId, groupId, periodId, techniqueId, teacherId);
    }
}