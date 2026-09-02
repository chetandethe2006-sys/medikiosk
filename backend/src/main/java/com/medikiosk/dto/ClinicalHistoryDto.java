package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalHistoryDto {
    private Long id;
    private Long patientId;
    private String sessionToken;
    private String chiefComplaint;
    private String complaintCategory;
    private String onsetAndDuration;
    private String painLocation;
    private String character;
    private Integer severityScale;
    private String radiation;
    private String aggravatingFactors;
    private String relievingFactors;
    private String associatedSymptoms;
    private String pastMedicalHistory;
    private String pastSurgicalHistory;
    private String currentMedications;
    private String drugAllergies;
    private String familyHistory;
    private String personalHistory;

    // AYUSH fields
    private Boolean ayushAssessed;
    private String prakriti;
    private String vikriti;
    private String agni;
    private String koshtha;
    private String aharaVihara;
    private String nidana;

    private Boolean redFlagTriggered;
    private List<HistoryAnswerDto> answers;
    private LocalDateTime recordedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryAnswerDto {
        private String questionKey;
        private String questionText;
        private String patientAnswer;
        private String inputMode;
        private Integer stepOrder;
    }
}
