package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorQueueItemDto {
    private Long sessionId;
    private Long patientId;
    private String tokenNumber; // #104
    private String patientName; // Sunita Patil
    private Integer age; // 52
    private String gender; // Female
    private String abhaId;
    private String chiefComplaint; // Chest Pain
    private String intakeStatus; // READY_FOR_DOCTOR, IN_PROGRESS, IN_CONSULTATION, COMPLETED
    private String riskLevel; // PRIORITY, REVIEW, NORMAL
    private Boolean redFlagTriggered;
    private Integer documentCount;
    private Boolean ayushMode;
    private Integer completenessPercentage;
    private LocalDateTime startedAt;
    private Long summaryId;
}
