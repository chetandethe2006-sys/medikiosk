package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSessionDto {
    private Long id;
    private String sessionToken;
    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private String tokenNumber;
    private String currentStep;
    private String selectedLanguage;
    private Boolean ayushMode;
    private String intakeStatus;
    private String riskLevel;
    private Integer completenessPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
