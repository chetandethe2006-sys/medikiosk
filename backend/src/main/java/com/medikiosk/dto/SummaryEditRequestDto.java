package com.medikiosk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummaryEditRequestDto {
    private String chiefComplaintText;
    private String historyOfPresentIllness;
    private String pastMedicalHistory;
    private String pastSurgicalHistory;
    private String drugAndAllergyHistory;
    private String currentMedications;
    private String potentialRedFlags;
    private String ayushAssessmentSummary;
    private String physicianNotes;
    private String doctorName;
    private String action; // SAVE_DRAFT, CONFIRM, REJECT
}
