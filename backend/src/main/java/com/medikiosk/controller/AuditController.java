package com.medikiosk.controller;

import com.medikiosk.dto.AuditEventDto;
import com.medikiosk.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AuditEventDto>> getPatientAuditLogs(@PathVariable Long patientId) {
        return ResponseEntity.ok(auditService.getAuditLogsForPatient(patientId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditEventDto>> getRecentAuditLogs() {
        return ResponseEntity.ok(auditService.getRecentAuditLogs());
    }
}
