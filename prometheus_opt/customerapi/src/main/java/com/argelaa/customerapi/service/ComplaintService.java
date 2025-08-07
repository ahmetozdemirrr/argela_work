package com.argelaa.customerapi.service;

import com.argelaa.customerapi.metric.KPIMetricsService;
import com.argelaa.customerapi.model.Complaint;
import com.argelaa.customerapi.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final KPIMetricsService kpiMetricsService;
    private final Random random = new Random();

    @Autowired
    public ComplaintService(ComplaintRepository complaintRepository, KPIMetricsService kpiMetricsService) {
        this.complaintRepository = complaintRepository;
        this.kpiMetricsService = kpiMetricsService;
    }

    @Transactional
    public Complaint createComplaint(Complaint complaint) {
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(LocalDateTime.now());
        Complaint savedComplaint = complaintRepository.save(complaint);

        // KPI 1: Toplam şikayet sayısını artır
        kpiMetricsService.incrementComplaints();

        return savedComplaint;
    }

    @Transactional
    public Optional<Complaint> resolveComplaint(Long complaintId) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isEmpty() || !"OPEN".equals(complaintOpt.get().getStatus())) {
            return Optional.empty(); // Şikayet bulunamadı veya zaten kapalı
        }

        Complaint complaint = complaintOpt.get();
        complaint.setStatus("RESOLVED");
        complaint.setResolvedAt(LocalDateTime.now());

        // Rastgele bir çözüm süresi ata (5 ile 120 saniye arasında)
        double handlingTime = 5 + (115 * random.nextDouble());
        complaint.setHandlingTimeSeconds(handlingTime);

        Complaint resolvedComplaint = complaintRepository.save(complaint);

        // KPI 2: Tamamlanan şikayet sayısını artır
        kpiMetricsService.incrementCompletedComplaints();

        // KPI 3 & 4: Şikayet işleme süresini kaydet
        kpiMetricsService.recordComplaintHandlingTime(handlingTime);

        return Optional.of(resolvedComplaint);
    }
}