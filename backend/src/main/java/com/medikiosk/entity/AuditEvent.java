package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private PatientSession session;

    @Column(nullable = false, length = 50)
    private String eventType; // CONSENT_GRANTED, HISTORY_STARTED, RED_FLAG_DETECTED, DOCUMENT_UPLOADED, DOCUMENT_PROCESSED, SUMMARY_GENERATED, SUMMARY_EDITED, SUMMARY_CONFIRMED, HIS_SYNCED

    @Column(nullable = false, length = 255)
    private String description;

    @Column(length = 50)
    @Builder.Default
    private String performedBy = "PATIENT_KIOSK"; // PATIENT_KIOSK, DR_SHARMA, SYSTEM_AI

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
