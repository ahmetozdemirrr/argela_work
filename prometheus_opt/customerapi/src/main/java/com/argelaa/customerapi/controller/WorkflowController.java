package com.argelaa.customerapi.controller;

import com.argelaa.customerapi.metric.KPIMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private KPIMetricsService kpiMetricsService;
    private final Random random = new Random();

    @PostMapping("/execute")
    public ResponseEntity<String> executeWorkflowStep(
            @RequestParam String workflowItem,
            @RequestParam String subscriberId) {

        // Rastgele bekleme süresi (1-300 saniye arası)
        long waitingTime = 1 + random.nextInt(300);

        kpiMetricsService.addUniqueSubscriberToWorkflow(subscriberId);
        kpiMetricsService.recordWorkflowWaitingTime(workflowItem, waitingTime);

        return ResponseEntity.ok("Workflow item '" + workflowItem + "' executed for subscriber '" + subscriberId + "' with waiting time " + waitingTime + "s.");
    }
}