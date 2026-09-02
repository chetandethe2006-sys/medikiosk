package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private PatientSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private ClinicalHistory history;

    // Standout 30-Second Clinical Quick View Fields
    @Column(columnDefinition = "TEXT")
    private String quickViewChiefComplaint; // "Chest pain for 2 days, substernal"

    @Column(columnDefinition = "TEXT")
    private String quickViewKeySymptoms; // "Breathlessness, diaphoresis, radiating to left arm"

    @Column(columnDefinition = "TEXT")
    private String quickViewRedFlagSummary; // "YES – Priority Review: Acute Angina / ACS symptoms"

    @Column(columnDefinition = "TEXT")
    private String quickViewPastHistory; // "Hypertension (5 yrs), T2DM (2 yrs)"

    @Column(columnDefinition = "TEXT")
    private String quickViewCurrentMeds; // "Tab Amlodipine 5mg OD"

    @Column(columnDefinition = "TEXT")
    private String quickViewRecentLabs; // "Hb 10.2 g/dL (Low), Fasting Glucose 138 mg/dL (High)"

    // Full Structured Sections (Physician Draft)
    @Column(columnDefinition = "TEXT")
    private String chiefComplaintText;

    @Column(columnDefinition = "TEXT")
    private String historyOfPresentIllness;

    @Column(columnDefinition = "TEXT")
    private String pastMedicalHistory;

    @Column(columnDefinition = "TEXT")
    private String pastSurgicalHistory;

    @Column(columnDefinition = "TEXT")
    private String drugAndAllergyHistory;

    @Column(columnDefinition = "TEXT")
    private String familyAndPersonalHistory;

    @Column(columnDefinition = "TEXT")
    private String reviewOfSystems;

    @Column(columnDefinition = "TEXT")
    private String previousInvestigations;

    @Column(columnDefinition = "TEXT")
    private String currentMedications;

    @Column(columnDefinition = "TEXT")
    private String potentialRedFlags;

    @Column(columnDefinition = "TEXT")
    private String ayushAssessmentSummary;

    @Column(columnDefinition = "TEXT")
    private String physicianNotes;

    // Safety & Governance Status
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "AI_DRAFT"; // AI_DRAFT, PHYSICIAN_EDITED, CONFIRMED, REJECTED

    @Column(nullable = false)
    @Builder.Default
    private String safetyNotice = "AI Generated Draft — Physician Verification Required before clinical decisions";

    @Column(length = 100)
    private String confirmedByDoctorName;

    private LocalDateTime confirmedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hisSynced = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean fhirResourceGenerated = false;

    @Column(columnDefinition = "TEXT")
    private String fhirBundleJson;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
