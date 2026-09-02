package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalSummaryDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String abhaId;
    private String tokenNumber;
    private String sessionToken;

    // 30-Second Quick View for Doctors
    private String quickViewChiefComplaint;
    private String quickViewKeySymptoms;
    private String quickViewRedFlagSummary;
    private String quickViewPastHistory;
    private String quickViewCurrentMeds;
    private String quickViewRecentLabs;

    // Full Detailed Sections
    private String chiefComplaintText;
    private String historyOfPresentIllness;
    private String pastMedicalHistory;
    private String pastSurgicalHistory;
    private String drugAndAllergyHistory;
    private String familyAndPersonalHistory;
    private String reviewOfSystems;
    private String previousInvestigations;
    private String currentMedications;
    private String potentialRedFlags;
    private String ayushAssessmentSummary;
    private String physicianNotes;

    // Safety & Governance
    private String status; // AI_DRAFT, PHYSICIAN_EDITED, CONFIRMED, REJECTED
    private String safetyNotice;
    private String confirmedByDoctorName;
    private LocalDateTime confirmedAt;
    private Boolean hisSynced;
    private Boolean fhirResourceGenerated;
    private String fhirBundleJson;
    private LocalDateTime generatedAt;
    private LocalDateTime updatedAt;
}
