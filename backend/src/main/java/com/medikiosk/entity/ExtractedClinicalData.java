package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extracted_clinical_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractedClinicalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private ClinicalDocument document;

    @Column(nullable = false, length = 100)
    private String parameterName; // e.g. "Hemoglobin", "Fasting Blood Sugar", "Serum Creatinine"

    @Column(nullable = false, length = 50)
    private String parameterValue; // e.g. "10.2", "138", "1.1"

    @Column(length = 30)
    private String unit; // e.g. "g/dL", "mg/dL", "U/L"

    @Column(length = 50)
    private String referenceRange; // e.g. "12.0 - 15.5", "70 - 99"

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAbnormal = false; // Flag if outside reference

    @Column(length = 20)
    private String abnormalDirection; // "LOW", "HIGH", "CRITICAL"

    @Column(length = 255)
    private String physicianNote;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime extractedAt = LocalDateTime.now();
}
