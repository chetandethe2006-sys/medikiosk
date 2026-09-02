package com.medikiosk.service;

import com.medikiosk.dto.RedFlagEventDto;
import com.medikiosk.entity.*;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.PatientSessionRepository;
import com.medikiosk.repository.RedFlagEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedFlagService {

    private final RedFlagEventRepository redFlagEventRepository;
    private final PatientSessionRepository patientSessionRepository;
    private final AuditService auditService;

    @Transactional
    public List<RedFlagEvent> evaluateSymptoms(ClinicalHistory history) {
        List<RedFlagEvent> detectedFlags = new ArrayList<>();
        if (history == null) return detectedFlags;

        String complaint = (history.getChiefComplaint() != null ? history.getChiefComplaint() : "").toLowerCase();
        String associated = (history.getAssociatedSymptoms() != null ? history.getAssociatedSymptoms() : "").toLowerCase();
        String onset = (history.getOnsetAndDuration() != null ? history.getOnsetAndDuration() : "").toLowerCase();
        Integer severity = history.getSeverityScale();

        // Rule 1: Chest pain + breathlessness or sweating
        if ((complaint.contains("chest") || complaint.contains("छाती")) &&
            (associated.contains("breathless") || associated.contains("sweat") || associated.contains("diaphoresis") || associated.contains("घाम") || associated.contains("धाप"))) {

            RedFlagEvent flag = RedFlagEvent.builder()
                .patient(history.getPatient())
                .session(history.getSession())
                .ruleKey("CHEST_PAIN_DYSPNEA_DIAPHORESIS")
                .severity("PRIORITY")
                .title("Potential Red Flag: Acute Coronary / Cardiorespiratory Risk")
                .symptomsReported("Chest pain with reported breathlessness and diaphoresis (sweating)")
                .clinicalRecommendation("Immediate clinical review recommended. Priority triage ticket generated. Urgent ECG & vital signs evaluation required.")
                .triageStatus("AWAITING_REVIEW")
                .detectedAt(LocalDateTime.now())
                .build();

            detectedFlags.add(flag);
        }

        // Rule 2: Sudden neurological symptoms / thunderclap headache
        if (complaint.contains("headache") && (onset.contains("thunderclap") || onset.contains("sudden") || (severity != null && severity >= 9))) {
            RedFlagEvent flag = RedFlagEvent.builder()
                .patient(history.getPatient())
                .session(history.getSession())
                .ruleKey("THUNDERCLAP_HEADACHE")
                .severity("PRIORITY")
                .title("Potential Red Flag: Acute Severe Thunderclap Headache")
                .symptomsReported("Sudden onset maximum intensity headache / Neurological alert")
                .clinicalRecommendation("Immediate clinical review recommended to exclude subarachnoid / intracranial pathology.")
                .triageStatus("AWAITING_REVIEW")
                .detectedAt(LocalDateTime.now())
                .build();

            detectedFlags.add(flag);
        }

        // Rule 3: High fever with severe prostration or petechiae/bleeding (Dengue/Sepsis alert)
        if (complaint.contains("fever") && (associated.contains("bleeding") || associated.contains("chills") && severity != null && severity >= 8)) {
            RedFlagEvent flag = RedFlagEvent.builder()
                .patient(history.getPatient())
                .session(history.getSession())
                .ruleKey("FEVER_WITH_WARNING_SIGNS")
                .severity("REVIEW")
                .title("Potential Warning: High Grade Pyrexia with Systemic Symptoms")
                .symptomsReported("High grade fever with severe body ache and warning symptoms")
                .clinicalRecommendation("Expedited clinical examination and complete blood count (CBC/Platelets) recommended.")
                .triageStatus("AWAITING_REVIEW")
                .detectedAt(LocalDateTime.now())
                .build();

            detectedFlags.add(flag);
        }

        if (!detectedFlags.isEmpty()) {
            history.setRedFlagTriggered(true);
            PatientSession session = history.getSession();
            if (session != null) {
                session.setRiskLevel("PRIORITY");
                patientSessionRepository.save(session);
            }

            for (RedFlagEvent flag : detectedFlags) {
                redFlagEventRepository.save(flag);
                auditService.logEvent(
                    history.getPatient(),
                    history.getSession(),
                    "RED_FLAG_DETECTED",
                    "Rule Triggered: " + flag.getTitle() + " (" + flag.getSeverity() + ")",
                    "AI_RULE_ENGINE",
                    "{\"ruleKey\":\"" + flag.getRuleKey() + "\"}"
                );
            }
        }

        return detectedFlags;
    }

    public List<RedFlagEventDto> getAllRedFlags() {
        return redFlagEventRepository.findAllByOrderByDetectedAtDesc()
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public List<RedFlagEventDto> getRedFlagsByPatient(Long patientId) {
        return redFlagEventRepository.findByPatientIdOrderByDetectedAtDesc(patientId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public List<RedFlagEventDto> getPendingTriageRedFlags() {
        return redFlagEventRepository.findByTriageStatusOrderByDetectedAtDesc("AWAITING_REVIEW")
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public RedFlagEventDto acknowledgeRedFlag(Long id, String doctorName) {
        RedFlagEvent event = redFlagEventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Red flag event not found: " + id));

        event.setTriageStatus("ACKNOWLEDGED");
        event.setAcknowledgedByDoctor(doctorName != null ? doctorName : "Dr. Sharma");
        event.setAcknowledgedAt(LocalDateTime.now());

        RedFlagEvent updated = redFlagEventRepository.save(event);

        auditService.logEvent(
            event.getPatient(),
            event.getSession(),
            "RED_FLAG_ACKNOWLEDGED",
            "Triage alert acknowledged by " + event.getAcknowledgedByDoctor(),
            event.getAcknowledgedByDoctor(),
            "{\"redFlagId\":" + id + "}"
        );

        return mapToDto(updated);
    }

    public RedFlagEventDto mapToDto(RedFlagEvent event) {
        return RedFlagEventDto.builder()
            .id(event.getId())
            .patientId(event.getPatient() != null ? event.getPatient().getId() : null)
            .patientName(event.getPatient() != null ? event.getPatient().getFullName() : "Unknown Patient")
            .patientAge(event.getPatient() != null ? event.getPatient().getAge() : null)
            .patientGender(event.getPatient() != null ? event.getPatient().getGender() : null)
            .sessionToken(event.getSession() != null ? event.getSession().getSessionToken() : null)
            .tokenNumber(event.getSession() != null ? event.getSession().getTokenNumber() : null)
            .ruleKey(event.getRuleKey())
            .severity(event.getSeverity())
            .title(event.getTitle())
            .symptomsReported(event.getSymptomsReported())
            .clinicalRecommendation(event.getClinicalRecommendation())
            .triageStatus(event.getTriageStatus())
            .acknowledgedByDoctor(event.getAcknowledgedByDoctor())
            .acknowledgedAt(event.getAcknowledgedAt())
            .detectedAt(event.getDetectedAt())
            .build();
    }
}
