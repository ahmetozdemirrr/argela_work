package com.argelaa.customerapi.metric;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ConcurrentUserTrackingService {

    @Autowired
    private KPIMetricsService kpiMetricsService;

    // Track active users
    private final Map<String, LocalDateTime> activeUsers = new ConcurrentHashMap<>();

    // Track active sessions
    private final Map<String, LocalDateTime> activeSessions = new ConcurrentHashMap<>();

    // Track agent sessions for KPI 5
    private final Map<String, LocalDateTime> agentSessions = new ConcurrentHashMap<>();

    // User login event
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        activeUsers.put(username, LocalDateTime.now());
        updateConcurrentUserCount();
    }

    // User logout event
    @EventListener
    public void handleLogoutSuccess(LogoutSuccessEvent event) {
        String username = event.getAuthentication().getName();
        activeUsers.remove(username);
        updateConcurrentUserCount();
    }

    // Session created event
    @EventListener
    public void handleSessionCreated(SessionCreatedEvent event) {
        String sessionId = event.getSessionId();
        activeSessions.put(sessionId, LocalDateTime.now());
        updateConcurrentUserCount();
    }

    // Session destroyed event
    @EventListener
    public void handleSessionDestroyed(SessionDestroyedEvent event) {
        String sessionId = event.getSessionId();
        activeSessions.remove(sessionId);
        updateConcurrentUserCount();
    }

    // Manual user tracking methods
    public void userLogin(String userId) {
        activeUsers.put(userId, LocalDateTime.now());
        updateConcurrentUserCount();
    }

    public void userLogout(String userId) {
        activeUsers.remove(userId);
        updateConcurrentUserCount();
    }

    // Agent session tracking for KPI 5
    public void startAgentSession(String agentId) {
        agentSessions.put(agentId, LocalDateTime.now());
        updateConcurrentUserCount();
    }

    public void endAgentSession(String agentId) {
        LocalDateTime startTime = agentSessions.remove(agentId);
        if (startTime != null) {
            // Calculate session duration
            long sessionDurationSeconds = java.time.Duration.between(startTime, LocalDateTime.now()).getSeconds();

            // KPI 5: Record agent session time
            kpiMetricsService.recordAgentSessionTime(agentId, sessionDurationSeconds);
        }
        updateConcurrentUserCount();
    }

    // Update concurrent user count
    private void updateConcurrentUserCount() {
        long currentUsers = Math.max(activeUsers.size(), activeSessions.size());

        // KPI 14 & 15: Update concurrent users
        kpiMetricsService.updateConcurrentUsers(currentUsers);
    }

    // Scheduled cleanup for inactive sessions (every 5 minutes)
    @Scheduled(fixedRate = 300000)
    public void cleanupInactiveSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30); // 30 minutes timeout

        // Clean up inactive users
        activeUsers.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));

        // Clean up inactive sessions
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));

        // Clean up inactive agent sessions
        agentSessions.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                // Record session time before removing
                long sessionDurationSeconds = java.time.Duration.between(entry.getValue(), LocalDateTime.now()).getSeconds();
                kpiMetricsService.recordAgentSessionTime(entry.getKey(), sessionDurationSeconds);
                return true;
            }
            return false;
        });

        updateConcurrentUserCount();
    }

    // Get current concurrent user count
    public long getCurrentConcurrentUsers() {
        return Math.max(activeUsers.size(), activeSessions.size());
    }

    // Get current agent count
    public long getCurrentAgentCount() {
        return agentSessions.size();
    }
}