package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.model.dto.statistic.DashboardDto;
import com.dinhngoctranduy.service.impl.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard() {
        DashboardDto dashboard = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(dashboard);
    }
}