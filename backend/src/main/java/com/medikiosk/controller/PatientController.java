package com.medikiosk.controller;

import com.medikiosk.dto.*;
import com.medikiosk.entity.Consent;
import com.medikiosk.service.ConsentService;
import com.medikiosk.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;
    private final ConsentService consentService;

    @PostMapping("/patients")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto dto) {
        PatientDto created = patientService.createOrGetPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/patients/abha/{abhaId}")
    public ResponseEntity<PatientDto> getPatientByAbha(@PathVariable String abhaId) {
        return ResponseEntity.ok(patientService.getPatientByAbha(abhaId));
    }

    @PostMapping("/sessions")
    public ResponseEntity<PatientSessionDto> startSession(@RequestBody Map<String, Object> body) {
        Long patientId = Long.valueOf(body.get("patientId").toString());
        String language = body.get("language") != null ? body.get("language").toString() : "en";
        Boolean ayushMode = body.get("ayushMode") != null && Boolean.parseBoolean(body.get("ayushMode").toString());

        PatientSessionDto session = patientService.startSession(patientId, language, ayushMode);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/sessions/{token}")
    public ResponseEntity<PatientSessionDto> getSession(@PathVariable String token) {
        return ResponseEntity.ok(patientService.getSessionByToken(token));
    }

    @PutMapping("/sessions/{token}/progress")
    public ResponseEntity<PatientSessionDto> updateProgress(
            @PathVariable String token,
            @RequestBody Map<String, Object> body) {
        String step = body.get("currentStep") != null ? body.get("currentStep").toString() : null;
        Integer completeness = body.get("completenessPercentage") != null ? Integer.valueOf(body.get("completenessPercentage").toString()) : null;

        return ResponseEntity.ok(patientService.updateSessionProgress(token, step, completeness));
    }

    @PostMapping("/consents")
    public ResponseEntity<Map<String, Object>> recordConsent(@RequestBody ConsentRequestDto dto) {
        Consent consent = consentService.recordConsent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "status", "CONSENT_RECORDED",
            "consentId", consent.getId(),
            "policyVersion", consent.getPolicyVersion(),
            "consentedAt", consent.getConsentedAt()
        ));
    }
}
