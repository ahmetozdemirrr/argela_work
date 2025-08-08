package com.argelaa.customerapi.controller;

import com.argelaa.customerapi.metric.KPIMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/siebel")
public class SiebelController {

    @Autowired
    private KPIMetricsService kpiMetricsService;

    @PostMapping("/create-ticket")
    public ResponseEntity<String> createTicket(@RequestParam String category) {
        kpiMetricsService.incrementTicketsToSiebel();
        kpiMetricsService.recordTicketDistribution(category);
        return ResponseEntity.ok("Siebel ticket created in category: " + category);
    }
}