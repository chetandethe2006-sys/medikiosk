package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private PatientSession session;

    @Column(nullable = false)
    @Builder.Default
    private Boolean dataCollectionConsented = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aiAssistanceConsented = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean documentExtractionConsented = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean audioExplanationHeard = false;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String policyVersion = "v2026.1-AIIA";

    @Column(length = 50)
    private String ipAddressOrKioskId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime consentedAt = LocalDateTime.now();
}
