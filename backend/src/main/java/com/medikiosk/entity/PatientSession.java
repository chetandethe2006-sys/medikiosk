package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sessionToken; // UUID / kiosk token

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String tokenNumber = "T-101"; // Hospital physical token e.g. #104

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String currentStep = "IDENTITY"; // LANGUAGE, CONSENT, IDENTITY, HISTORY, DOCUMENTS, REVIEW, COMPLETED

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String selectedLanguage = "en";

    @Column(nullable = false)
    @Builder.Default
    private Boolean ayushMode = false;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String intakeStatus = "IN_PROGRESS"; // IN_PROGRESS, READY_FOR_DOCTOR, IN_CONSULTATION, COMPLETED

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String riskLevel = "NORMAL"; // NORMAL, REVIEW, PRIORITY

    @Column(nullable = false)
    @Builder.Default
    private Integer completenessPercentage = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    private LocalDateTime completedAt;
}
