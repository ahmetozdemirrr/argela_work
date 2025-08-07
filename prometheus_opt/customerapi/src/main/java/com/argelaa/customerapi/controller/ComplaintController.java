package com.argelaa.customerapi.controller;

import com.argelaa.customerapi.model.Complaint;
import com.argelaa.customerapi.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint) {
        if (complaint.getCustomerId() == null || complaint.getComplaintText() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Complaint createdComplaint = complaintService.createComplaint(complaint);
        return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Complaint> resolveComplaint(@PathVariable Long id) {
        return complaintService.resolveComplaint(id)
                .map(complaint -> new ResponseEntity<>(complaint, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}