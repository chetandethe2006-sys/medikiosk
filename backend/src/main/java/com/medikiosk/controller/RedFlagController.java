package com.medikiosk.controller;

import com.medikiosk.dto.RedFlagEventDto;
import com.medikiosk.service.RedFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/red-flags")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RedFlagController {

    private final RedFlagService redFlagService;

    @GetMapping
    public ResponseEntity<List<RedFlagEventDto>> getAllRedFlags() {
        return ResponseEntity.ok(redFlagService.getAllRedFlags());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RedFlagEventDto>> getPendingTriageRedFlags() {
        return ResponseEntity.ok(redFlagService.getPendingTriageRedFlags());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RedFlagEventDto>> getRedFlagsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(redFlagService.getRedFlagsByPatient(patientId));
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<RedFlagEventDto> acknowledgeRedFlag(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String doctorName = body != null && body.get("doctorName") != null ? body.get("doctorName") : "Dr. Rajesh Sharma";
        return ResponseEntity.ok(redFlagService.acknowledgeRedFlag(id, doctorName));
    }
}
