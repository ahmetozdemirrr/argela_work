package com.argelaa.customerapi.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


@Component
public class KPIMetricsService {

    private final MeterRegistry meterRegistry;

    // KPI 1: Number of Complaints
    private final AtomicLong totalComplaints = new AtomicLong(0);

    // KPI 2: Number of Completed Complaint Process
    private final AtomicLong completedComplaints = new AtomicLong(0);

    // KPI 3 & 4: Complaint Handling Time
    private final List<Double> complaintHandlingTimes = new ArrayList<>();

    // KPI 5: Average Session Time by Agent
    private final Map<String, List<Double>> agentSessionTimes = new ConcurrentHashMap<>();

    // KPI 6 & 7: Complaint Handling Time by Type
    private final Map<String, List<Double>> handlingTimesByType = new ConcurrentHashMap<>();

    // KPI 8: Number of Ticket to Siebel
    private final AtomicLong ticketsToSiebel = new AtomicLong(0);

    // KPI 9: Number Distribution of Tickets to Siebel
    private final Map<String, AtomicLong> ticketDistribution = new ConcurrentHashMap<>();

    // KPI 10: Complaint Handling - Number of Times Each Item in Teknik Destek
    private final Map<String, AtomicLong> teknikkDeskItems = new ConcurrentHashMap<>();

    // KPI 12: Total Waiting time in Each WorkFlow Item
    private final Map<String, AtomicLong> workflowWaitingTimes = new ConcurrentHashMap<>();

    // KPI 13: Number of Unique Subscribers WorkFlow Run
    private final Set<String> uniqueSubscribersWorkflow = ConcurrentHashMap.newKeySet();

    // KPI 14: Number of Concurrent User
    private final AtomicLong concurrentUsers = new AtomicLong(0);

    // KPI 15: Number of Max Concurrent Users
    private final AtomicLong maxConcurrentUsers = new AtomicLong(0);

    public KPIMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initializeGauges();
    }

    private void initializeGauges() {
        // KPI 1: Number of Complaints
        Gauge.builder("kpi_total_complaints",totalComplaints,AtomicLong::get)
                .description("Total number of complaints")
                .register(meterRegistry);

        // KPI 2: Number of Completed Complaint Process
        Gauge.builder("kpi_completed_complaints", completedComplaints, AtomicLong::get)
                .description("Number of completed complaint processes")
                .register(meterRegistry);

        // KPI 3: Complaint Handling Time (Current)
        Gauge.builder("kpi_current_complaint_handling_time_seconds",this, KPIMetricsService::getCurrentComplaintHandlingTime)
                .description("Current complaint handling time in seconds")
                .register(meterRegistry);

        // KPI 4: Average Complaint Handling Time
        Gauge.builder("kpi_average_complaint_handling_time_seconds",this, KPIMetricsService::getAverageComplaintHandlingTime)
                .description("Average complaint handling time in seconds")
                .register(meterRegistry);

        // KPI 8: Number of Ticket to Siebel
        Gauge.builder("kpi_tickets_to_siebel", ticketsToSiebel, AtomicLong::get)
                .description("Number of tickets sent to Siebel")
                .register(meterRegistry);

        // KPI 14: Number of Concurrent User
        Gauge.builder("kpi_concurrent_users", concurrentUsers, AtomicLong::get)
                .description("Current number of concurrent users")
                .register(meterRegistry);

        // KPI 15: Number of Max Concurrent Users
        Gauge.builder("kpi_max_concurrent_users", maxConcurrentUsers, AtomicLong::get)
                .description("Maximum number of concurrent users")
                .register(meterRegistry);

        // KPI 13: Number of Unique Subscribers WorkFlow Run
        Gauge.builder("kpi_unique_subscribers_workflow", uniqueSubscribersWorkflow, Set::size)
                .description("Number of unique subscribers in workflow")
                .register(meterRegistry);
    }

    // KPI 1: Increment complaints
    public void incrementComplaints() {
        totalComplaints.incrementAndGet();
    }

    // KPI 2: Increment completed complaints
    public void incrementCompletedComplaints() {
        completedComplaints.incrementAndGet();
    }

    // KPI 3 & 4: Record complaint handling time
    public void recordComplaintHandlingTime(double timeInSeconds) {
        synchronized (complaintHandlingTimes) {
            complaintHandlingTimes.add(timeInSeconds);
        }
    }

    // KPI 5: Record agent session time
    public void recordAgentSessionTime(String agentId, double sessionTimeInSeconds) {
        agentSessionTimes.computeIfAbsent(agentId, k -> new ArrayList<>()).add(sessionTimeInSeconds);

        // Create gauge for this agent if not exists
        Gauge.builder("kpi_agent_average_session_time_seconds", agentId, this::getAverageSessionTimeForAgent)
                .description("Average session time for agent")
                .tag("agent_id", agentId)
                .register(meterRegistry);
    }

    // KPI 6 & 7: Record complaint handling time by type
    public void recordComplaintHandlingTimeByType(String complaintType, double timeInSeconds) {
        handlingTimesByType.computeIfAbsent(complaintType, k -> new ArrayList<>()).add(timeInSeconds);

        // Create gauges for this type if not exists
        Gauge.builder("kpi_complaint_handling_time_by_type_seconds", complaintType, this::getCurrentHandlingTimeByType)
                .description("Current complaint handling time by type")
                .tag("complaint_type", complaintType)
                .register(meterRegistry);

        Gauge.builder("kpi_average_complaint_handling_time_by_type_seconds", complaintType, this::getAverageHandlingTimeByType)
                .description("Average complaint handling time by type")
                .tag("complaint_type", complaintType)
                .register(meterRegistry);
    }

    // KPI 8: Increment tickets to Siebel
    public void incrementTicketsToSiebel() {
        ticketsToSiebel.incrementAndGet();
    }

    // KPI 9: Record ticket distribution to Siebel
    public void recordTicketDistribution(String category) {
        ticketDistribution.computeIfAbsent(category, k -> new AtomicLong(0)).incrementAndGet();

        // Create gauge for this category if not exists
        Gauge.builder("kpi_ticket_distribution_siebel", category, this::getTicketDistributionByCategory)
                .description("Number of tickets distributed to Siebel by category")
                .tag("category", category)
                .register(meterRegistry);
    }

    // KPI 10: Record technical support item usage
    public void recordTechnicalSupportItem(String itemName) {
        teknikkDeskItems.computeIfAbsent(itemName, k -> new AtomicLong(0)).incrementAndGet();

        // Create gauge for this item if not exists
        Gauge.builder("kpi_technical_support_item_usage", itemName, this::getTechnicalSupportItemUsage)
                .description("Number of times technical support item is used")
                .tag("item_name", itemName)
                .register(meterRegistry);
    }

    // KPI 12: Record workflow waiting time
    public void recordWorkflowWaitingTime(String workflowItem, long waitingTimeInSeconds) {
        workflowWaitingTimes.computeIfAbsent(workflowItem, k -> new AtomicLong(0))
                .addAndGet(waitingTimeInSeconds);
        // Create gauge for this workflow item if not exists
        Gauge.builder("kpi_workflow_waiting_time_seconds", workflowItem, this::getWorkflowWaitingTime)
                .description("Total waiting time in workflow item")
                .tag("workflow_item", workflowItem)
                .register(meterRegistry);
    }

    // KPI 13: Add unique subscriber to workflow
    public void addUniqueSubscriberToWorkflow(String subscriberId) {
        uniqueSubscribersWorkflow.add(subscriberId);
    }

    // KPI 14: Update concurrent users
    public void updateConcurrentUsers(long currentUsers) {
        concurrentUsers.set(currentUsers);

        // KPI 15: Update max concurrent users if needed
        long currentMax = maxConcurrentUsers.get();
        if (currentUsers > currentMax) {
            maxConcurrentUsers.set(currentUsers);
        }
    }

    // Helper methods for gauge calculations
    private double getCurrentComplaintHandlingTime() {
        synchronized (complaintHandlingTimes) {
            return complaintHandlingTimes.isEmpty() ? 0.0 :
                    complaintHandlingTimes.get(complaintHandlingTimes.size() - 1);
        }
    }

    private double getAverageComplaintHandlingTime() {
        synchronized (complaintHandlingTimes) {
            return complaintHandlingTimes.isEmpty() ? 0.0 :
                    complaintHandlingTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }
    }

    private double getAverageSessionTimeForAgent(String agentId) {
        List<Double> times = agentSessionTimes.get(agentId);
        return times == null || times.isEmpty() ? 0.0 :
                times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double getCurrentHandlingTimeByType(String complaintType) {
        List<Double> times = handlingTimesByType.get(complaintType);
        return times == null || times.isEmpty() ? 0.0 : times.get(times.size() - 1);
    }

    private double getAverageHandlingTimeByType(String complaintType) {
        List<Double> times = handlingTimesByType.get(complaintType);
        return times == null || times.isEmpty() ? 0.0 :
                times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double getTicketDistributionByCategory(String category) {
        AtomicLong count = ticketDistribution.get(category);
        return count == null ? 0.0 : count.get();
    }

    private double getTechnicalSupportItemUsage(String itemName) {
        AtomicLong count = teknikkDeskItems.get(itemName);
        return count == null ? 0.0 : count.get();
    }

    private double getWorkflowWaitingTime(String workflowItem) {
        AtomicLong time = workflowWaitingTimes.get(workflowItem);
        return time == null ? 0.0 : time.get();
    }

    // Scheduled method to clean up old data (optional)
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupOldData() {
        // Implement cleanup logic if needed to prevent memory leaks
        // For example, keep only last 1000 complaint handling times
        synchronized (complaintHandlingTimes) {
            if (complaintHandlingTimes.size() > 1000) {
                complaintHandlingTimes.subList(0, complaintHandlingTimes.size() - 1000).clear();
            }
        }
    }
}