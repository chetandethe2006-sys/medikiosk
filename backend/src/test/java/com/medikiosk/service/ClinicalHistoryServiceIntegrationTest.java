package com.medikiosk.service;

import com.medikiosk.dto.*;
import com.medikiosk.entity.Patient;
import com.medikiosk.entity.PatientSession;
import com.medikiosk.repository.PatientRepository;
import com.medikiosk.repository.PatientSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClinicalHistoryServiceIntegrationTest {

    @Autowired
    private ClinicalHistoryService clinicalHistoryService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientSessionRepository sessionRepository;

    private PatientSession session;

    @BeforeEach
    void setUp() {
        Patient patient = patientRepository.save(Patient.builder()
            .fullName("Aarav Sharma")
            .age(35)
            .gender("Male")
            .phone("+91 9876543210")
            .preferredLanguage("en")
            .build());

        session = sessionRepository.save(PatientSession.builder()
            .patient(patient)
            .sessionToken("test-intake-token-555")
            .tokenNumber("#555")
            .selectedLanguage("en")
            .currentStep("HISTORY")
            .build());
    }

    @Test
    void testCompleteFiveQuestionFlowEndToEnd() {
        // Step 1: Start Interview
        HistoryStartRequestDto startReq = HistoryStartRequestDto.builder()
            .sessionToken("test-intake-token-555")
            .language("en")
            .build();

        HistoryStepResponseDto step1 = clinicalHistoryService.startHistory(startReq);
        assertNotNull(step1);
        assertEquals(1, step1.getCurrentStepNumber());
        assertEquals(5, step1.getTotalEstimatedSteps());
        assertEquals(20, step1.getCompletenessPercentage());
        assertFalse(step1.getIsCompleted());
        assertEquals("CHIEF_COMPLAINT", step1.getQuestionKey());
        assertEquals("What is your main problem or complaint?", step1.getQuestionText());
        assertEquals(List.of("Pain", "Fever", "Cough / Cold", "Stomach problem", "Other"), step1.getQuickOptions());

        // Step 2: Submit Q1 Answer ("Pain")
        HistoryStepResponseDto step2 = clinicalHistoryService.submitAnswer(
            "test-intake-token-555",
            HistoryAnswerRequestDto.builder()
                .questionKey("CHIEF_COMPLAINT")
                .questionText(step1.getQuestionText())
                .patientAnswer("Pain")
                .inputMode("TAP")
                .stepOrder(1)
                .build()
        );
        assertEquals(2, step2.getCurrentStepNumber());
        assertEquals(5, step2.getTotalEstimatedSteps());
        assertEquals(40, step2.getCompletenessPercentage());
        assertFalse(step2.getIsCompleted());
        assertEquals("ONSET", step2.getQuestionKey());
        assertEquals("When did this problem start?", step2.getQuestionText());
        assertEquals(List.of("Today", "Yesterday", "2–3 days ago", "More than a week ago", "More than a month ago"), step2.getQuickOptions());

        // Step 3: Submit Q2 Answer ("Yesterday")
        HistoryStepResponseDto step3 = clinicalHistoryService.submitAnswer(
            "test-intake-token-555",
            HistoryAnswerRequestDto.builder()
                .questionKey("ONSET")
                .questionText(step2.getQuestionText())
                .patientAnswer("Yesterday")
                .inputMode("TAP")
                .stepOrder(2)
                .build()
        );
        assertEquals(3, step3.getCurrentStepNumber());
        assertEquals(5, step3.getTotalEstimatedSteps());
        assertEquals(60, step3.getCompletenessPercentage());
        assertFalse(step3.getIsCompleted());
        assertEquals("SEVERITY", step3.getQuestionKey());
        assertEquals("How severe is your problem on a scale of 0 to 10?", step3.getQuestionText());
        assertEquals(List.of("0–2 Mild", "3–5 Moderate", "6–8 Severe", "9–10 Very severe"), step3.getQuickOptions());

        // Step 4: Submit Q3 Answer ("6–8 Severe")
        HistoryStepResponseDto step4 = clinicalHistoryService.submitAnswer(
            "test-intake-token-555",
            HistoryAnswerRequestDto.builder()
                .questionKey("SEVERITY")
                .questionText(step3.getQuestionText())
                .patientAnswer("6–8 Severe")
                .inputMode("TAP")
                .stepOrder(3)
                .build()
        );
        assertEquals(4, step4.getCurrentStepNumber());
        assertEquals(5, step4.getTotalEstimatedSteps());
        assertEquals(80, step4.getCompletenessPercentage());
        assertFalse(step4.getIsCompleted());
        assertEquals("ASSOCIATED_SYMPTOMS", step4.getQuestionKey());
        assertEquals("Do you have any other symptoms?", step4.getQuestionText());
        assertEquals(List.of("None", "Weakness / Tiredness", "Nausea / Vomiting", "Dizziness", "Breathing difficulty"), step4.getQuickOptions());

        // Step 5: Submit Q4 Answer ("Breathing difficulty")
        HistoryStepResponseDto step5 = clinicalHistoryService.submitAnswer(
            "test-intake-token-555",
            HistoryAnswerRequestDto.builder()
                .questionKey("ASSOCIATED_SYMPTOMS")
                .questionText(step4.getQuestionText())
                .patientAnswer("Breathing difficulty")
                .inputMode("TAP")
                .stepOrder(4)
                .build()
        );
        assertEquals(5, step5.getCurrentStepNumber());
        assertEquals(5, step5.getTotalEstimatedSteps());
        assertEquals(100, step5.getCompletenessPercentage());
        assertFalse(step5.getIsCompleted());
        assertEquals("PAST_HISTORY", step5.getQuestionKey());
        assertEquals("Do you have any existing medical conditions or take regular medicines?", step5.getQuestionText());
        assertEquals(List.of("No", "Diabetes", "High Blood Pressure", "Heart condition", "Other"), step5.getQuickOptions());

        // Step 6: Submit Q5 Answer ("High Blood Pressure") -> Must complete!
        HistoryStepResponseDto completedStep = clinicalHistoryService.submitAnswer(
            "test-intake-token-555",
            HistoryAnswerRequestDto.builder()
                .questionKey("PAST_HISTORY")
                .questionText(step5.getQuestionText())
                .patientAnswer("High Blood Pressure")
                .inputMode("TAP")
                .stepOrder(5)
                .build()
        );
        assertEquals(5, completedStep.getCurrentStepNumber());
        assertEquals(5, completedStep.getTotalEstimatedSteps());
        assertEquals(100, completedStep.getCompletenessPercentage());
        assertTrue(completedStep.getIsCompleted());
        assertEquals("COMPLETED", completedStep.getQuestionKey());
        assertTrue(completedStep.getQuickOptions().isEmpty());

        // Verify session updated step to DOCUMENTS
        PatientSession updatedSession = sessionRepository.findBySessionToken("test-intake-token-555").orElseThrow();
        assertEquals("DOCUMENTS", updatedSession.getCurrentStep());
        assertEquals(100, updatedSession.getCompletenessPercentage());

        // Verify captured history state
        ClinicalHistoryDto historyDto = clinicalHistoryService.getHistoryBySessionToken("test-intake-token-555");
        assertEquals("Pain", historyDto.getChiefComplaint());
        assertEquals("Yesterday", historyDto.getOnsetAndDuration());
        assertEquals(7, historyDto.getSeverityScale());
        assertEquals("Breathing difficulty", historyDto.getAssociatedSymptoms());
        assertEquals("High Blood Pressure", historyDto.getPastMedicalHistory());
        assertEquals(5, historyDto.getAnswers().size());
    }
}
