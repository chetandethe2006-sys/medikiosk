package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalHistory {

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
    private String chiefComplaint; // e.g. "Chest pain since yesterday"

    @Column(length = 100)
    private String complaintCategory; // CHEST_PAIN, FEVER, ABDOMINAL_PAIN, HEADACHE, COUGH, DIABETES, GENERAL

    @Column(length = 100)
    private String onsetAndDuration; // e.g. "2 days ago, acute onset"

    @Column(length = 100)
    private String painLocation; // e.g. "Substernal, retrosternal"

    @Column(length = 100)
    private String character; // e.g. "Constricting, crushing, aching"

    private Integer severityScale; // 0 to 10

    @Column(length = 100)
    private String radiation; // e.g. "Left arm, jaw"

    @Column(columnDefinition = "TEXT")
    private String aggravatingFactors; // e.g. "Exertion, walking up stairs"

    @Column(columnDefinition = "TEXT")
    private String relievingFactors; // e.g. "Rest, sublingual tablet"

    @Column(columnDefinition = "TEXT")
    private String associatedSymptoms; // e.g. "Breathlessness, diaphoresis/sweating, dizziness"

    @Column(columnDefinition = "TEXT")
    private String pastMedicalHistory; // e.g. "Hypertension (5 yrs), T2DM (2 yrs)"

    @Column(columnDefinition = "TEXT")
    private String pastSurgicalHistory; // e.g. "Appendectomy (2018)"

    @Column(columnDefinition = "TEXT")
    private String currentMedications; // e.g. "Tab Amlodipine 5mg OD, Tab Metformin 500mg BD"

    @Column(columnDefinition = "TEXT")
    private String drugAllergies; // e.g. "Penicillin (rash)"

    @Column(columnDefinition = "TEXT")
    private String familyHistory; // e.g. "Father had CAD at 55"

    @Column(columnDefinition = "TEXT")
    private String personalHistory; // e.g. "Non-smoker, vegetarian, normal sleep"

    // AYUSH Specific Fields
    private Boolean ayushAssessed;
    @Column(length = 255)
    private String prakriti; // Vata-Pitta, Pitta-Kapha, etc.
    @Column(length = 255)
    private String vikriti; // Vata Vriddhi, Pitta Prakopa
    @Column(length = 255)
    private String agni; // Sama, Vishama, Tikshna, Manda
    @Column(length = 255)
    private String koshtha; // Mridu, Krura, Madhyama
    @Column(columnDefinition = "TEXT")
    private String aharaVihara; // Dietary habits, lifestyle, sleep pattern
    @Column(columnDefinition = "TEXT")
    private String nidana; // Aetiological factors

    @Column(nullable = false)
    @Builder.Default
    private Boolean redFlagTriggered = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();
}
