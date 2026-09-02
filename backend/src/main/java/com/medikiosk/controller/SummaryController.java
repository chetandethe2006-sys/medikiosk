package com.medikiosk.controller;

import com.medikiosk.dto.ClinicalSummaryDto;
import com.medikiosk.dto.FhirBundleDto;
import com.medikiosk.dto.SummaryEditRequestDto;
import com.medikiosk.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping("/generate")
    public ResponseEntity<ClinicalSummaryDto> generateSummary(@RequestBody Map<String, String> body) {
        String sessionToken = body.get("sessionToken");
        return ResponseEntity.ok(summaryService.generateSummaryForSession(sessionToken));
    }

    @GetMapping("/session/{sessionToken}")
    public ResponseEntity<ClinicalSummaryDto> getSummaryBySession(@PathVariable String sessionToken) {
        return ResponseEntity.ok(summaryService.getSummaryBySessionToken(sessionToken));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ClinicalSummaryDto> getSummaryByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(summaryService.getSummaryByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicalSummaryDto> editSummary(
            @PathVariable Long id,
            @RequestBody SummaryEditRequestDto edit) {
        return ResponseEntity.ok(summaryService.editSummary(id, edit));
    }

    @PostMapping("/{id}/sync-his")
    public ResponseEntity<FhirBundleDto> syncToHIS(@PathVariable Long id) {
        return ResponseEntity.ok(summaryService.syncToHIS(id));
    }
}
