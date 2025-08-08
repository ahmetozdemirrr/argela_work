package com.argelaa.customerapi.controller;

import com.argelaa.customerapi.metric.KPIMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/technical-support")
public class TechnicalSupportController {

    @Autowired
    private KPIMetricsService kpiMetricsService;

    @PostMapping("/use-item")
    public ResponseEntity<String> useTechnicalSupportItem(@RequestParam String itemName) {
        kpiMetricsService.recordTechnicalSupportItem(itemName);
        return ResponseEntity.ok("Technical support item used: " + itemName);
    }
}