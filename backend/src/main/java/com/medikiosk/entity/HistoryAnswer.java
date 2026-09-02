package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "history_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id")
    private ClinicalHistory history;

    @Column(nullable = false, length = 64)
    private String questionKey; // e.g. "CHIEF_COMPLAINT", "ONSET", "SEVERITY", "ASSOCIATED_SYMPTOMS"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String patientAnswer;

    @Column(length = 20)
    @Builder.Default
    private String inputMode = "TAP"; // TAP, TEXT, VOICE

    @Column(nullable = false)
    @Builder.Default
    private Integer stepOrder = 1;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime answeredAt = LocalDateTime.now();
}
