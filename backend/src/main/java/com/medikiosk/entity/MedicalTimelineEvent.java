package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_timeline_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalTimelineEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false, length = 100)
    private String title; // e.g. "Laboratory Blood Panel", "Cardiology OPD Follow-up"

    @Column(nullable = false, length = 50)
    private String category; // LAB_REPORT, PRESCRIPTION, DISCHARGE, SURGERY, VACCINATION, AYUSH_CONSULT

    @Column(columnDefinition = "TEXT")
    private String summary; // e.g. "Hb 10.2 g/dL, Fasting Sugar 138 mg/dL"

    @Column(columnDefinition = "TEXT")
    private String medications; // e.g. "Tab Telmisartan 40mg, Tab Metformin 500mg"

    @Column(length = 100)
    private String facilityOrDoctor; // e.g. "AIIA New Delhi - OPD 4"

    @Column(length = 255)
    private String documentRef;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
