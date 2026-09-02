package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private PatientSession session;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false, length = 50)
    private String documentType; // PRESCRIPTION, LAB_REPORT, DISCHARGE_SUMMARY, IMAGING_REPORT

    @Column(length = 20)
    private String fileExtension; // pdf, jpg, png

    private Long fileSizeBytes;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String processingStatus = "EXTRACTED"; // UPLOADING, PROCESSING, EXTRACTED, NEEDS_REVIEW, FAILED

    @Column(columnDefinition = "TEXT")
    private String rawOcrText;

    private LocalDateTime documentDate;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
