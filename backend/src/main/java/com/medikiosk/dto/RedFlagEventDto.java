package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedFlagEventDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String sessionToken;
    private String tokenNumber;
    private String ruleKey;
    private String severity; // PRIORITY, REVIEW, MODERATE
    private String title;
    private String symptomsReported;
    private String clinicalRecommendation;
    private String triageStatus; // AWAITING_REVIEW, ACKNOWLEDGED, ESCALATED, RESOLVED
    private String acknowledgedByDoctor;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime detectedAt;
}
