package com.argelaa.customerapi.controller;

import com.argelaa.customerapi.metric.ConcurrentUserTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    @Autowired
    private ConcurrentUserTrackingService concurrentUserTrackingService;

    @PostMapping("/{agentId}/start-session")
    public ResponseEntity<String> startAgentSession(@PathVariable String agentId) {
        concurrentUserTrackingService.startAgentSession(agentId);
        return ResponseEntity.ok("Agent session started for: " + agentId);
    }

    @PostMapping("/{agentId}/end-session")
    public ResponseEntity<String> endAgentSession(@PathVariable String agentId) {
        concurrentUserTrackingService.endAgentSession(agentId);
        return ResponseEntity.ok("Agent session ended for: " + agentId);
    }
}