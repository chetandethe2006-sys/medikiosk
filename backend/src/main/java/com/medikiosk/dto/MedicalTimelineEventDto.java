package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalTimelineEventDto {
    private Long id;
    private Long patientId;
    private LocalDate eventDate;
    private String title;
    private String category; // LAB_REPORT, PRESCRIPTION, DISCHARGE, SURGERY, VACCINATION, AYUSH_CONSULT
    private String summary;
    private String medications;
    private String facilityOrDoctor;
    private String documentRef;
    private LocalDateTime createdAt;
}
