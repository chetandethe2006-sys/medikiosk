package com.medikiosk.service;

import com.medikiosk.dto.ConsentRequestDto;
import com.medikiosk.entity.Consent;
import com.medikiosk.entity.Patient;
import com.medikiosk.entity.PatientSession;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.ConsentRepository;
import com.medikiosk.repository.PatientRepository;
import com.medikiosk.repository.PatientSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final AuditService auditService;

    @Transactional
    public Consent recordConsent(ConsentRequestDto request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + request.getPatientId()));

        PatientSession session = null;
        if (request.getSessionToken() != null && !request.getSessionToken().isBlank()) {
            session = sessionRepository.findBySessionToken(request.getSessionToken()).orElse(null);
        }

        Consent consent = Consent.builder()
            .patient(patient)
            .session(session)
            .dataCollectionConsented(Boolean.TRUE.equals(request.getDataCollectionConsented()))
            .aiAssistanceConsented(Boolean.TRUE.equals(request.getAiAssistanceConsented()))
            .documentExtractionConsented(Boolean.TRUE.equals(request.getDocumentExtractionConsented()))
            .audioExplanationHeard(Boolean.TRUE.equals(request.getAudioExplanationHeard()))
            .policyVersion(request.getPolicyVersion() != null ? request.getPolicyVersion() : "v2026.1-AIIA")
            .ipAddressOrKioskId(request.getKioskId() != null ? request.getKioskId() : "KIOSK-TERMINAL-01")
            .consentedAt(LocalDateTime.now())
            .build();

        Consent saved = consentRepository.save(consent);

        auditService.logEvent(
            patient,
            session,
            "CONSENT_GRANTED",
            "Patient granted intake and AI processing consent (Policy: " + consent.getPolicyVersion() + ")",
            "PATIENT_KIOSK",
            "{\"audioExplanationHeard\":" + consent.getAudioExplanationHeard() + "}"
        );

        return saved;
    }

    public boolean hasPatientConsented(Long patientId) {
        return consentRepository.findByPatientIdOrderByConsentedAtDesc(patientId).isPresent();
    }
}
