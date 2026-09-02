package com.medikiosk.service;

import com.medikiosk.ai.ClinicalSummaryService;
import com.medikiosk.dto.ClinicalSummaryDto;
import com.medikiosk.dto.FhirBundleDto;
import com.medikiosk.dto.SummaryEditRequestDto;
import com.medikiosk.entity.*;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final ClinicalSummaryRepository summaryRepository;
    private final PatientSessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final ClinicalHistoryRepository historyRepository;
    private final ClinicalDocumentRepository documentRepository;
    private final ExtractedClinicalDataRepository extractedDataRepository;
    private final RedFlagEventRepository redFlagRepository;
    private final ClinicalSummaryService clinicalSummaryService;
    private final FHIRMapper fhirMapper;
    private final AuditService auditService;

    @Transactional
    public ClinicalSummaryDto generateSummaryForSession(String sessionToken) {
        PatientSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionToken));

        Patient patient = session.getPatient();
        ClinicalHistory history = historyRepository.findBySessionId(session.getId()).orElse(null);
        List<ClinicalDocument> documents = documentRepository.findBySessionIdOrderByUploadedAtDesc(session.getId());
        List<ExtractedClinicalData> extracted = documents.stream()
            .flatMap(d -> extractedDataRepository.findByDocumentId(d.getId()).stream())
            .toList();
        List<RedFlagEvent> redFlags = redFlagRepository.findBySessionId(session.getId());

        ClinicalSummary summary = clinicalSummaryService.generateSummary(patient, session, history, documents, extracted, redFlags);

        // Check if previous summary exists, overwrite or save
        summaryRepository.findBySessionId(session.getId()).ifPresent(existing -> summary.setId(existing.getId()));

        ClinicalSummary saved = summaryRepository.save(summary);

        session.setCurrentStep("REVIEW");
        session.setIntakeStatus("READY_FOR_DOCTOR");
        sessionRepository.save(session);

        auditService.logEvent(
            patient,
            session,
            "SUMMARY_GENERATED",
            "AI structured physician summary generated (Draft Status)",
            "AI_SUMMARY_ENGINE",
            "{\"summaryId\":" + saved.getId() + "}"
        );

        return mapToDto(saved);
    }

    public ClinicalSummaryDto getSummaryBySessionToken(String sessionToken) {
        PatientSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionToken));

        ClinicalSummary summary = summaryRepository.findBySessionId(session.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Clinical summary not found for session: " + sessionToken));

        return mapToDto(summary);
    }

    public ClinicalSummaryDto getSummaryByPatientId(Long patientId) {
        ClinicalSummary summary = summaryRepository.findTopByPatientIdOrderByGeneratedAtDesc(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Clinical summary not found for patient: " + patientId));

        return mapToDto(summary);
    }

    @Transactional
    public ClinicalSummaryDto editSummary(Long id, SummaryEditRequestDto edit) {
        ClinicalSummary summary = summaryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Summary not found: " + id));

        if (edit.getChiefComplaintText() != null) summary.setChiefComplaintText(edit.getChiefComplaintText());
        if (edit.getHistoryOfPresentIllness() != null) summary.setHistoryOfPresentIllness(edit.getHistoryOfPresentIllness());
        if (edit.getPastMedicalHistory() != null) summary.setPastMedicalHistory(edit.getPastMedicalHistory());
        if (edit.getPastSurgicalHistory() != null) summary.setPastSurgicalHistory(edit.getPastSurgicalHistory());
        if (edit.getDrugAndAllergyHistory() != null) summary.setDrugAndAllergyHistory(edit.getDrugAndAllergyHistory());
        if (edit.getCurrentMedications() != null) summary.setCurrentMedications(edit.getCurrentMedications());
        if (edit.getPotentialRedFlags() != null) summary.setPotentialRedFlags(edit.getPotentialRedFlags());
        if (edit.getAyushAssessmentSummary() != null) summary.setAyushAssessmentSummary(edit.getAyushAssessmentSummary());
        if (edit.getPhysicianNotes() != null) summary.setPhysicianNotes(edit.getPhysicianNotes());

        if ("CONFIRM".equalsIgnoreCase(edit.getAction())) {
            summary.setStatus("CONFIRMED");
            summary.setConfirmedByDoctorName(edit.getDoctorName() != null ? edit.getDoctorName() : "Dr. Rajesh Sharma");
            summary.setConfirmedAt(LocalDateTime.now());

            // Prepare Mock FHIR resource
            FhirBundleDto bundle = fhirMapper.createFhirBundle(summary);
            summary.setFhirResourceGenerated(true);
            summary.setFhirBundleJson(bundle.getMessage());

            if (summary.getSession() != null) {
                summary.getSession().setIntakeStatus("COMPLETED");
                sessionRepository.save(summary.getSession());
            }

            auditService.logEvent(
                summary.getPatient(),
                summary.getSession(),
                "SUMMARY_CONFIRMED",
                "Clinical summary verified and confirmed by " + summary.getConfirmedByDoctorName(),
                summary.getConfirmedByDoctorName(),
                "{\"status\":\"CONFIRMED\"}"
            );
        } else {
            summary.setStatus("PHYSICIAN_EDITED");
            summary.setUpdatedAt(LocalDateTime.now());

            auditService.logEvent(
                summary.getPatient(),
                summary.getSession(),
                "SUMMARY_EDITED",
                "Physician updated clinical draft sections",
                edit.getDoctorName() != null ? edit.getDoctorName() : "Dr. Sharma",
                "{\"status\":\"PHYSICIAN_EDITED\"}"
            );
        }

        ClinicalSummary saved = summaryRepository.save(summary);
        return mapToDto(saved);
    }

    @Transactional
    public FhirBundleDto syncToHIS(Long summaryId) {
        ClinicalSummary summary = summaryRepository.findById(summaryId)
            .orElseThrow(() -> new ResourceNotFoundException("Summary not found: " + summaryId));

        summary.setHisSynced(true);
        summary.setFhirResourceGenerated(true);
        ClinicalSummary saved = summaryRepository.save(summary);

        auditService.logEvent(
            saved.getPatient(),
            saved.getSession(),
            "HIS_SYNCED",
            "Clinical Case Summary synced to Hospital Information System (HIS / ABDM Sandbox Mock)",
            "SYSTEM_INTEGRATION",
            "{\"abdmFacilityId\":\"IN-MH-AIIA-0021\"}"
        );

        return fhirMapper.createFhirBundle(saved);
    }

    public ClinicalSummaryDto mapToDto(ClinicalSummary s) {
        return ClinicalSummaryDto.builder()
            .id(s.getId())
            .patientId(s.getPatient() != null ? s.getPatient().getId() : null)
            .patientName(s.getPatient() != null ? s.getPatient().getFullName() : null)
            .patientAge(s.getPatient() != null ? s.getPatient().getAge() : null)
            .patientGender(s.getPatient() != null ? s.getPatient().getGender() : null)
            .abhaId(s.getPatient() != null ? s.getPatient().getAbhaId() : null)
            .tokenNumber(s.getSession() != null ? s.getSession().getTokenNumber() : null)
            .sessionToken(s.getSession() != null ? s.getSession().getSessionToken() : null)
            .quickViewChiefComplaint(s.getQuickViewChiefComplaint())
            .quickViewKeySymptoms(s.getQuickViewKeySymptoms())
            .quickViewRedFlagSummary(s.getQuickViewRedFlagSummary())
            .quickViewPastHistory(s.getQuickViewPastHistory())
            .quickViewCurrentMeds(s.getQuickViewCurrentMeds())
            .quickViewRecentLabs(s.getQuickViewRecentLabs())
            .chiefComplaintText(s.getChiefComplaintText())
            .historyOfPresentIllness(s.getHistoryOfPresentIllness())
            .pastMedicalHistory(s.getPastMedicalHistory())
            .pastSurgicalHistory(s.getPastSurgicalHistory())
            .drugAndAllergyHistory(s.getDrugAndAllergyHistory())
            .familyAndPersonalHistory(s.getFamilyAndPersonalHistory())
            .reviewOfSystems(s.getReviewOfSystems())
            .previousInvestigations(s.getPreviousInvestigations())
            .currentMedications(s.getCurrentMedications())
            .potentialRedFlags(s.getPotentialRedFlags())
            .ayushAssessmentSummary(s.getAyushAssessmentSummary())
            .physicianNotes(s.getPhysicianNotes())
            .status(s.getStatus())
            .safetyNotice(s.getSafetyNotice())
            .confirmedByDoctorName(s.getConfirmedByDoctorName())
            .confirmedAt(s.getConfirmedAt())
            .hisSynced(s.getHisSynced())
            .fhirResourceGenerated(s.getFhirResourceGenerated())
            .fhirBundleJson(s.getFhirBundleJson())
            .generatedAt(s.getGeneratedAt())
            .updatedAt(s.getUpdatedAt())
            .build();
    }
}
