package com.medikiosk.service;

import com.medikiosk.ai.ClinicalSummaryService;
import com.medikiosk.ai.DocumentExtractionService;
import com.medikiosk.entity.*;
import com.medikiosk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final ConsentRepository consentRepository;
    private final ClinicalHistoryRepository historyRepository;
    private final HistoryAnswerRepository answerRepository;
    private final ClinicalDocumentRepository documentRepository;
    private final ExtractedClinicalDataRepository extractedDataRepository;
    private final MedicalTimelineEventRepository timelineRepository;
    private final ClinicalSummaryRepository summaryRepository;
    private final RedFlagEventRepository redFlagRepository;
    private final DoctorRepository doctorRepository;
    private final AuditEventRepository auditRepository;
    private final ClinicalSummaryService clinicalSummaryService;
    private final DocumentExtractionService documentExtractionService;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Database already seeded with demo data.");
            return;
        }

        log.info("Seeding MediKiosk database with realistic Indian hospital clinical scenarios...");

        // 1. Seed Doctor
        Doctor doctor = Doctor.builder()
            .fullName("Dr. Rajesh Sharma, MD")
            .department("Internal Medicine & Kayachikitsa")
            .opdRoom("OPD Room 104 — AIIA")
            .email("dr.sharma@aiia.gov.in")
            .isAvailable(true)
            .createdAt(LocalDateTime.now())
            .build();
        doctor = doctorRepository.save(doctor);
        authenticationService.registerUser("dr.sharma@aiia.gov.in", "password123", "ROLE_DOCTOR", doctor.getId());

        // 2. Primary Demo Patient: Sunita Patil (52F, Chest Pain + Red Flag + Documents + Timeline + Summary)
        seedSunitaPatil();

        // 3. Demo Patient 2: Rahul More (28M, Fever / Dengue suspect)
        seedRahulMore();

        // 4. Demo Patient 3: Asha Kulkarni (45F, Diabetes Follow-up + AYUSH Assessment Mode)
        seedAshaKulkarni();

        // 5. Demo Patient 4: Vijay Shinde (63M, COPD / Chronic Cough)
        seedVijayShinde();

        log.info("Database seeding completed successfully. Ready for SIH demo.");
    }

    private void seedSunitaPatil() {
        Patient sunita = Patient.builder()
            .abhaId("91-4567-8901-2345")
            .fullName("Sunita Patil")
            .age(52)
            .gender("Female")
            .phone("+91 98201 45678")
            .address("Flat 402, Shivshahi Tower, Dadar, Mumbai - 400014")
            .emergencyContact("+91 98201 45679 (Suresh Patil - Husband)")
            .bloodGroup("B+")
            .preferredLanguage("mr")
            .createdAt(LocalDateTime.now().minusHours(2))
            .build();
        sunita = patientRepository.save(sunita);
        authenticationService.registerUser("sunita@aiia.gov.in", "password123", "ROLE_PATIENT", sunita.getId());

        PatientSession session = PatientSession.builder()
            .sessionToken("sunita-session-token-104")
            .patient(sunita)
            .tokenNumber("#104")
            .currentStep("REVIEW")
            .selectedLanguage("mr")
            .ayushMode(true)
            .intakeStatus("READY_FOR_DOCTOR")
            .riskLevel("PRIORITY")
            .completenessPercentage(92)
            .startedAt(LocalDateTime.now().minusMinutes(25))
            .build();
        session = sessionRepository.save(session);

        Consent consent = Consent.builder()
            .patient(sunita)
            .session(session)
            .dataCollectionConsented(true)
            .aiAssistanceConsented(true)
            .documentExtractionConsented(true)
            .audioExplanationHeard(true)
            .policyVersion("v2026.1-AIIA")
            .ipAddressOrKioskId("KIOSK-TERMINAL-01")
            .consentedAt(LocalDateTime.now().minusMinutes(24))
            .build();
        consentRepository.save(consent);

        ClinicalHistory history = ClinicalHistory.builder()
            .patient(sunita)
            .session(session)
            .chiefComplaint("Chest pain since yesterday")
            .complaintCategory("CHEST_PAIN")
            .onsetAndDuration("Started yesterday afternoon (approx 24 hours)")
            .painLocation("Substernal (Center of chest), radiating to left arm and neck")
            .character("Heavy crushing sensation / constricting pressure")
            .severityScale(8)
            .radiation("Left arm, jaw, and interscapular region")
            .aggravatingFactors("Worsens on climbing stairs and physical exertion")
            .relievingFactors("Partial temporary relief with complete rest")
            .associatedSymptoms("Breathlessness (Dyspnea on exertion), profuse sweating (diaphoresis), mild nausea")
            .pastMedicalHistory("Hypertension (diagnosed 5 years ago), Type 2 Diabetes Mellitus (2 years)")
            .pastSurgicalHistory("Tubectomy (2002), uneventful")
            .currentMedications("Tab Amlodipine 5mg OD (morning), Tab Metformin 500mg BD")
            .drugAllergies("No Known Drug Allergies (NKDA)")
            .familyHistory("Father had ischemic heart disease (Myocardial Infarction at 56)")
            .personalHistory("Vegetarian diet, non-smoker, mild insomnia due to stress")
            // AYUSH details
            .ayushAssessed(true)
            .prakriti("Pitta-Vata")
            .vikriti("Vata-Pitta Prakopa (Hridroga Poorvaroopa)")
            .agni("Vishama Agni (Irregular digestive fire)")
            .koshtha("Madhyama Koshtha")
            .aharaVihara("Ruksha and Tikshna Ahara, irregular meal timings, disturbed sleep pattern")
            .nidana("Atichintana (Mental stress), Vyayama Abhava (Sedentary lifestyle)")
            .redFlagTriggered(true)
            .recordedAt(LocalDateTime.now().minusMinutes(18))
            .build();
        history = historyRepository.save(history);

        // History Answer Q&A logs
        saveAnswer(history, "CHIEF_COMPLAINT", "What brings you to the hospital today?", "I have chest pain since yesterday.", "VOICE", 1);
        saveAnswer(history, "ONSET", "When did this chest pain start, and did it come on suddenly or gradually?", "Started yesterday afternoon", "TAP", 2);
        saveAnswer(history, "LOCATION", "Where exactly do you feel the pain, and does it spread anywhere?", "Center of chest (Substernal) radiating to left arm", "TAP", 3);
        saveAnswer(history, "CHARACTER", "What does the pain feel like?", "Heavy pressure / crushing sensation", "TAP", 4);
        saveAnswer(history, "SEVERITY", "How severe is the pain on a scale of 0 to 10?", "8 / 10 (Severe)", "TAP", 5);
        saveAnswer(history, "ASSOCIATED_SYMPTOMS", "Are you experiencing any other symptoms?", "Breathlessness & Sweating (Diaphoresis)", "TAP", 6);
        saveAnswer(history, "PAST_HISTORY", "Do you have existing medical conditions or medications?", "Hypertension & Diabetes on medication", "TAP", 7);
        saveAnswer(history, "AYUSH_PRAKRITI", "AYUSH Mode: What is your body constitution / weather tolerance?", "Pitta (Sensitive to Heat, Sweating)", "TAP", 8);

        // Red Flag Event for Sunita
        RedFlagEvent redFlag = RedFlagEvent.builder()
            .patient(sunita)
            .session(session)
            .ruleKey("CHEST_PAIN_DYSPNEA_DIAPHORESIS")
            .severity("PRIORITY")
            .title("Potential Red Flag: Acute Coronary / Cardiorespiratory Risk")
            .symptomsReported("Chest pain (Severity 8/10) with substernal radiation, breathlessness, and diaphoresis (sweating)")
            .clinicalRecommendation("Immediate clinical review recommended. Priority triage ticket generated. Urgent 12-lead ECG, blood pressure check, and Cardiac Troponin I indicated.")
            .triageStatus("AWAITING_REVIEW")
            .detectedAt(LocalDateTime.now().minusMinutes(15))
            .build();
        redFlag = redFlagRepository.save(redFlag);

        // Uploaded Document 1: Biochemical Lab Report
        ClinicalDocument labDoc = ClinicalDocument.builder()
            .patient(sunita)
            .session(session)
            .fileName("Lab_Report_12_July.pdf")
            .documentType("LAB_REPORT")
            .fileExtension("pdf")
            .fileSizeBytes(342000L)
            .processingStatus("EXTRACTED")
            .documentDate(LocalDateTime.now().minusDays(15))
            .uploadedAt(LocalDateTime.now().minusMinutes(12))
            .build();
        labDoc = documentRepository.save(labDoc);

        List<ExtractedClinicalData> labData = documentExtractionService.extractStructuredData(labDoc);
        for (ExtractedClinicalData d : labData) {
            extractedDataRepository.save(d);
        }

        // Uploaded Document 2: Previous Prescription
        ClinicalDocument rxDoc = ClinicalDocument.builder()
            .patient(sunita)
            .session(session)
            .fileName("Prescription_Cardiology_AIIA.jpg")
            .documentType("PRESCRIPTION")
            .fileExtension("jpg")
            .fileSizeBytes(198000L)
            .processingStatus("EXTRACTED")
            .documentDate(LocalDateTime.now().minusMonths(3))
            .uploadedAt(LocalDateTime.now().minusMinutes(10))
            .build();
        rxDoc = documentRepository.save(rxDoc);

        List<ExtractedClinicalData> rxData = documentExtractionService.extractStructuredData(rxDoc);
        for (ExtractedClinicalData d : rxData) {
            extractedDataRepository.save(d);
        }

        // Medical Timeline Events
        timelineRepository.save(MedicalTimelineEvent.builder()
            .patient(sunita)
            .eventDate(LocalDate.now().minusDays(15))
            .title("Biochemical & Metabolic Blood Panel")
            .category("LAB_REPORT")
            .summary("Hb 10.2 g/dL (Mild Anemia), Fasting Sugar 138 mg/dL (High), S. Creatinine 1.1 mg/dL, Total Cholesterol 215 mg/dL")
            .medications("Ongoing: Amlodipine 5mg OD")
            .facilityOrDoctor("AIIA Central Diagnostic Laboratory")
            .documentRef("Lab_Report_12_July.pdf")
            .createdAt(LocalDateTime.now().minusDays(15))
            .build());

        timelineRepository.save(MedicalTimelineEvent.builder()
            .patient(sunita)
            .eventDate(LocalDate.now().minusMonths(3))
            .title("Cardiology OPD Prescription & Follow-up")
            .category("PRESCRIPTION")
            .summary("Blood Pressure: 146/92 mmHg. Advised lifestyle modifications and dose optimization.")
            .medications("Tab Amlodipine 5mg OD, Tab Metformin 500mg BD")
            .facilityOrDoctor("Dr. R. Sharma — AIIA OPD 104")
            .documentRef("Prescription_Cardiology_AIIA.jpg")
            .createdAt(LocalDateTime.now().minusMonths(3))
            .build());

        timelineRepository.save(MedicalTimelineEvent.builder()
            .patient(sunita)
            .eventDate(LocalDate.of(2025, 6, 18))
            .title("2D Echocardiography & Color Doppler")
            .category("IMAGING_REPORT")
            .summary("Left Ventricular Ejection Fraction (LVEF): 55%. Normal LV cavity size. No regional wall motion abnormalities at rest.")
            .medications("Tab Amlodipine 5mg OD")
            .facilityOrDoctor("AIIA Non-Invasive Cardiology Unit")
            .documentRef("Echo_Report_Jun2025.pdf")
            .createdAt(LocalDateTime.now().minusMonths(14))
            .build());

        // Generate Structured Summary for Sunita
        ClinicalSummary summary = clinicalSummaryService.generateSummary(
            sunita, session, history, List.of(labDoc, rxDoc), labData, List.of(redFlag)
        );
        summaryRepository.save(summary);

        // Audit Trail
        logAudit(sunita, session, "CONSENT_GRANTED", "Patient granted intake and AI processing consent", "PATIENT_KIOSK");
        logAudit(sunita, session, "HISTORY_STARTED", "Clinical history interview initiated for complaint: Chest pain", "PATIENT_KIOSK");
        logAudit(sunita, session, "RED_FLAG_DETECTED", "Rule Triggered: Potential Red Flag: Acute Coronary / Cardiorespiratory Risk (PRIORITY)", "AI_RULE_ENGINE");
        logAudit(sunita, session, "DOCUMENT_PROCESSED", "Digitized Lab_Report_12_July.pdf (4 parameters extracted)", "AI_OCR_ENGINE");
        logAudit(sunita, session, "SUMMARY_GENERATED", "Physician draft summary generated with 30-Second Quick View", "AI_SUMMARY_ENGINE");
    }

    private void seedRahulMore() {
        Patient rahul = Patient.builder()
            .abhaId("91-1234-5678-9012")
            .fullName("Rahul More")
            .age(28)
            .gender("Male")
            .phone("+91 97654 32100")
            .address("B-12, Green Park, Kothrud, Pune - 411038")
            .emergencyContact("+91 97654 32101 (Pooja More - Sister)")
            .bloodGroup("O+")
            .preferredLanguage("hi")
            .createdAt(LocalDateTime.now().minusHours(3))
            .build();
        rahul = patientRepository.save(rahul);

        PatientSession session = PatientSession.builder()
            .sessionToken("rahul-session-token-105")
            .patient(rahul)
            .tokenNumber("#105")
            .currentStep("REVIEW")
            .selectedLanguage("hi")
            .ayushMode(false)
            .intakeStatus("READY_FOR_DOCTOR")
            .riskLevel("REVIEW")
            .completenessPercentage(85)
            .startedAt(LocalDateTime.now().minusMinutes(40))
            .build();
        session = sessionRepository.save(session);

        ClinicalHistory history = ClinicalHistory.builder()
            .patient(rahul)
            .session(session)
            .chiefComplaint("High fever with severe body ache for 3 days")
            .complaintCategory("FEVER")
            .onsetAndDuration("3 days ago, high continuous grade")
            .painLocation("Generalized severe myalgia, retro-orbital pain")
            .character("High fever (103°F) with shivering and chills")
            .severityScale(7)
            .associatedSymptoms("Severe retro-orbital eye pain, extreme fatigue, nausea")
            .pastMedicalHistory("No significant past chronic illness")
            .currentMedications("Tab Paracetamol 650mg SOS")
            .drugAllergies("No Known Drug Allergies")
            .redFlagTriggered(false)
            .recordedAt(LocalDateTime.now().minusMinutes(30))
            .build();
        history = historyRepository.save(history);

        ClinicalSummary summary = ClinicalSummary.builder()
            .patient(rahul)
            .session(session)
            .history(history)
            .quickViewChiefComplaint("High fever with chills for 3 days (103°F)")
            .quickViewKeySymptoms("Retro-orbital pain, severe body aches, nausea")
            .quickViewRedFlagSummary("REVIEW – Dengue / Acute Pyrexia Evaluation needed")
            .quickViewPastHistory("Nil significant")
            .quickViewCurrentMeds("Tab Paracetamol 650mg SOS")
            .quickViewRecentLabs("CBC (Pending) — Platelet check ordered")
            .chiefComplaintText("High fever for 3 days with intense headache and body aches")
            .historyOfPresentIllness("Rahul More, 28M, presents with acute onset high grade fever (103°F) for 3 days with chills, severe retro-orbital headache, and muscle pain. Suspected acute arboviral (Dengue/Chikungunya) infection.")
            .pastMedicalHistory("None")
            .currentMedications("Tab Paracetamol 650mg SOS")
            .potentialRedFlags("Watch for Dengue warning signs: abdominal pain, persistent vomiting, mucosal bleeding.")
            .status("AI_DRAFT")
            .safetyNotice("AI Generated Draft — Physician Verification Required before clinical decisions")
            .generatedAt(LocalDateTime.now().minusMinutes(25))
            .build();
        summaryRepository.save(summary);
    }

    private void seedAshaKulkarni() {
        Patient asha = Patient.builder()
            .abhaId("91-8899-7766-5544")
            .fullName("Asha Kulkarni")
            .age(45)
            .gender("Female")
            .phone("+91 98450 11223")
            .address("Plot 14, Ayodhya Nagar, Nagpur - 440024")
            .bloodGroup("A+")
            .preferredLanguage("en")
            .createdAt(LocalDateTime.now().minusHours(4))
            .build();
        asha = patientRepository.save(asha);

        PatientSession session = PatientSession.builder()
            .sessionToken("asha-session-token-106")
            .patient(asha)
            .tokenNumber("#106")
            .currentStep("REVIEW")
            .selectedLanguage("en")
            .ayushMode(true)
            .intakeStatus("READY_FOR_DOCTOR")
            .riskLevel("NORMAL")
            .completenessPercentage(90)
            .startedAt(LocalDateTime.now().minusMinutes(50))
            .build();
        session = sessionRepository.save(session);

        ClinicalHistory history = ClinicalHistory.builder()
            .patient(asha)
            .session(session)
            .chiefComplaint("Diabetes follow-up and chronic fatigue")
            .complaintCategory("DIABETES")
            .onsetAndDuration("Known T2DM for 3 years, fatigue worsening since 2 months")
            .severityScale(4)
            .associatedSymptoms("Polyuria at night, lethargy, dry mouth")
            .pastMedicalHistory("Type 2 Diabetes Mellitus (3 yrs), Mild Dyslipidemia")
            .currentMedications("Tab Metformin 500mg BD")
            .ayushAssessed(true)
            .prakriti("Kapha-Pitta")
            .vikriti("Kapha-Medo Dushti (Prameha Poorvaroopa)")
            .agni("Manda Agni (Sluggish digestion, heaviness after food)")
            .koshtha("Mridu Koshtha")
            .aharaVihara("High carbohydrate diet, sweets craving, sedentary lifestyle, afternoon sleep (Divasvapna)")
            .nidana("Avyayama (lack of exercise), Madhura Ahara Atisevana")
            .redFlagTriggered(false)
            .recordedAt(LocalDateTime.now().minusMinutes(45))
            .build();
        history = historyRepository.save(history);

        ClinicalSummary summary = ClinicalSummary.builder()
            .patient(asha)
            .session(session)
            .history(history)
            .quickViewChiefComplaint("Diabetes follow-up, daytime lethargy (2 months)")
            .quickViewKeySymptoms("Fatigue, polyuria, post-meal heaviness")
            .quickViewRedFlagSummary("NO – Stable chronic follow-up")
            .quickViewPastHistory("Type 2 Diabetes (3 years)")
            .quickViewCurrentMeds("Tab Metformin 500mg BD")
            .quickViewRecentLabs("HbA1c: 7.6% (Suboptimal control)")
            .chiefComplaintText("Routine diabetes follow-up with complaints of fatigue and post-prandial heaviness")
            .historyOfPresentIllness("Patient presents for quarterly diabetic review. Reports suboptimal glycemic control and daytime fatigue. Interested in integrative AYUSH lifestyle management.")
            .pastMedicalHistory("Type 2 Diabetes Mellitus")
            .currentMedications("Tab Metformin 500mg BD")
            .potentialRedFlags("None. Normal vital signs.")
            .ayushAssessmentSummary("Prakriti: Kapha-Pitta | Vikriti: Medo-Dhatu Dushti (Prameha) | Agni: Manda Agni | Advised Pathya-Apathya diet and Vyayama yoga.")
            .status("AI_DRAFT")
            .safetyNotice("AI Generated Draft — Physician Verification Required before clinical decisions")
            .generatedAt(LocalDateTime.now().minusMinutes(40))
            .build();
        summaryRepository.save(summary);
    }

    private void seedVijayShinde() {
        Patient vijay = Patient.builder()
            .abhaId("91-3322-1144-7788")
            .fullName("Vijay Shinde")
            .age(63)
            .gender("Male")
            .phone("+91 99220 88776")
            .address("House 78, Shivaji Nagar, Nashik - 422002")
            .bloodGroup("AB+")
            .preferredLanguage("en")
            .createdAt(LocalDateTime.now().minusHours(5))
            .build();
        vijay = patientRepository.save(vijay);

        PatientSession session = PatientSession.builder()
            .sessionToken("vijay-session-token-107")
            .patient(vijay)
            .tokenNumber("#107")
            .currentStep("REVIEW")
            .selectedLanguage("en")
            .ayushMode(false)
            .intakeStatus("READY_FOR_DOCTOR")
            .riskLevel("REVIEW")
            .completenessPercentage(88)
            .startedAt(LocalDateTime.now().minusMinutes(60))
            .build();
        sessionRepository.save(session);
    }

    private void saveAnswer(ClinicalHistory history, String key, String question, String answer, String mode, int step) {
        HistoryAnswer a = HistoryAnswer.builder()
            .history(history)
            .questionKey(key)
            .questionText(question)
            .patientAnswer(answer)
            .inputMode(mode)
            .stepOrder(step)
            .answeredAt(LocalDateTime.now().minusMinutes(20 - step))
            .build();
        answerRepository.save(a);
    }

    private void logAudit(Patient p, PatientSession s, String type, String desc, String performedBy) {
        auditRepository.save(AuditEvent.builder()
            .patient(p)
            .session(s)
            .eventType(type)
            .description(desc)
            .performedBy(performedBy)
            .timestamp(LocalDateTime.now().minusMinutes(10))
            .build());
    }
}
