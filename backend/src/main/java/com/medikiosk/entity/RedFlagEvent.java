package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "red_flag_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedFlagEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private PatientSession session;

    @Column(nullable = false, length = 100)
    private String ruleKey; // e.g. "CHEST_PAIN_DYSPNEA_DIAPHORESIS", "SUDDEN_NEUROLOGIC_DEFICIT"

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "PRIORITY"; // PRIORITY, REVIEW, MODERATE

    @Column(nullable = false, length = 150)
    private String title; // "Potential Red Flag: Acute Chest Pain Complex"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String symptomsReported; // "Chest pain + breathlessness + sweating"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String clinicalRecommendation; // "Immediate clinical review recommended. Priority triage ticket dispatched."

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String triageStatus = "AWAITING_REVIEW"; // AWAITING_REVIEW, ACKNOWLEDGED, ESCALATED, RESOLVED

    @Column(length = 100)
    private String acknowledgedByDoctor;

    private LocalDateTime acknowledgedAt;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime detectedAt = LocalDateTime.now();
}
