package com.medikiosk.ai;

import com.medikiosk.dto.HistoryAnswerRequestDto;
import com.medikiosk.dto.HistoryStartRequestDto;
import com.medikiosk.dto.HistoryStepResponseDto;
import com.medikiosk.entity.ClinicalHistory;
import com.medikiosk.entity.Patient;
import com.medikiosk.entity.PatientSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MockAIHistoryServiceTest {

    private MockAIHistoryService service;
    private ClinicalHistory history;

    @BeforeEach
    void setUp() {
        service = new MockAIHistoryService();
        Patient patient = Patient.builder().id(1L).fullName("Test Patient").build();
        PatientSession session = PatientSession.builder().id(1L).sessionToken("test-session-123").patient(patient).build();
        history = ClinicalHistory.builder().id(1L).patient(patient).session(session).build();
    }

    @Test
    void testFiveQuestionFlowSequentialProgression() {
        // Step 1: Start interview
        HistoryStartRequestDto startReq = HistoryStartRequestDto.builder()
            .sessionToken("test-session-123")
            .language("en")
            .build();

        HistoryStepResponseDto step1 = service.startInterview(startReq, history);
        assertEquals(1, step1.getCurrentStepNumber());
        assertEquals(5, step1.getTotalEstimatedSteps());
        assertEquals(20, step1.getCompletenessPercentage());
        assertFalse(step1.getIsCompleted());
        assertEquals("CHIEF_COMPLAINT", step1.getQuestionKey());
        assertEquals("What is your main problem or complaint?", step1.getQuestionText());
        assertEquals(List.of("Pain", "Fever", "Cough / Cold", "Stomach problem", "Other"), step1.getQuickOptions());

        // Step 2: Answer Q1
        HistoryAnswerRequestDto ans1 = HistoryAnswerRequestDto.builder()
            .questionKey("CHIEF_COMPLAINT")
            .patientAnswer("Pain")
            .stepOrder(1)
            .build();

        HistoryStepResponseDto step2 = service.processAnswer(ans1, history, "en", false);
        assertEquals("Pain", history.getChiefComplaint());
        assertEquals(2, step2.getCurrentStepNumber());
        assertEquals(5, step2.getTotalEstimatedSteps());
        assertEquals(40, step2.getCompletenessPercentage());
        assertFalse(step2.getIsCompleted());
        assertEquals("ONSET", step2.getQuestionKey());
        assertEquals("When did this problem start?", step2.getQuestionText());
        assertEquals(List.of("Today", "Yesterday", "2–3 days ago", "More than a week ago", "More than a month ago"), step2.getQuickOptions());

        // Step 3: Answer Q2
        HistoryAnswerRequestDto ans2 = HistoryAnswerRequestDto.builder()
            .questionKey("ONSET")
            .patientAnswer("2–3 days ago")
            .stepOrder(2)
            .build();

        HistoryStepResponseDto step3 = service.processAnswer(ans2, history, "en", false);
        assertEquals("2–3 days ago", history.getOnsetAndDuration());
        assertEquals(3, step3.getCurrentStepNumber());
        assertEquals(5, step3.getTotalEstimatedSteps());
        assertEquals(60, step3.getCompletenessPercentage());
        assertFalse(step3.getIsCompleted());
        assertEquals("SEVERITY", step3.getQuestionKey());
        assertEquals("How severe is your problem on a scale of 0 to 10?", step3.getQuestionText());
        assertEquals(List.of("0–2 Mild", "3–5 Moderate", "6–8 Severe", "9–10 Very severe"), step3.getQuickOptions());

        // Step 4: Answer Q3
        HistoryAnswerRequestDto ans3 = HistoryAnswerRequestDto.builder()
            .questionKey("SEVERITY")
            .patientAnswer("6–8 Severe")
            .stepOrder(3)
            .build();

        HistoryStepResponseDto step4 = service.processAnswer(ans3, history, "en", false);
        assertEquals(7, history.getSeverityScale());
        assertEquals(4, step4.getCurrentStepNumber());
        assertEquals(5, step4.getTotalEstimatedSteps());
        assertEquals(80, step4.getCompletenessPercentage());
        assertFalse(step4.getIsCompleted());
        assertEquals("ASSOCIATED_SYMPTOMS", step4.getQuestionKey());
        assertEquals("Do you have any other symptoms?", step4.getQuestionText());
        assertEquals(List.of("None", "Weakness / Tiredness", "Nausea / Vomiting", "Dizziness", "Breathing difficulty"), step4.getQuickOptions());

        // Step 5: Answer Q4
        HistoryAnswerRequestDto ans4 = HistoryAnswerRequestDto.builder()
            .questionKey("ASSOCIATED_SYMPTOMS")
            .patientAnswer("Breathing difficulty")
            .stepOrder(4)
            .build();

        HistoryStepResponseDto step5 = service.processAnswer(ans4, history, "en", false);
        assertEquals("Breathing difficulty", history.getAssociatedSymptoms());
        assertEquals(5, step5.getCurrentStepNumber());
        assertEquals(5, step5.getTotalEstimatedSteps());
        assertEquals(100, step5.getCompletenessPercentage());
        assertFalse(step5.getIsCompleted());
        assertEquals("PAST_HISTORY", step5.getQuestionKey());
        assertEquals("Do you have any existing medical conditions or take regular medicines?", step5.getQuestionText());
        assertEquals(List.of("No", "Diabetes", "High Blood Pressure", "Heart condition", "Other"), step5.getQuickOptions());

        // Step 6: Answer Q5 -> Interview Must Complete
        HistoryAnswerRequestDto ans5 = HistoryAnswerRequestDto.builder()
            .questionKey("PAST_HISTORY")
            .patientAnswer("High Blood Pressure")
            .stepOrder(5)
            .build();

        HistoryStepResponseDto stepCompleted = service.processAnswer(ans5, history, "en", false);
        assertEquals("High Blood Pressure", history.getPastMedicalHistory());
        assertEquals("High Blood Pressure", history.getCurrentMedications());
        assertEquals(5, stepCompleted.getCurrentStepNumber());
        assertEquals(5, stepCompleted.getTotalEstimatedSteps());
        assertEquals(100, stepCompleted.getCompletenessPercentage());
        assertTrue(stepCompleted.getIsCompleted());
        assertEquals("COMPLETED", stepCompleted.getQuestionKey());
        assertTrue(stepCompleted.getQuickOptions().isEmpty());
    }

    @Test
    void testHindiLanguageSupport() {
        HistoryStartRequestDto startReq = HistoryStartRequestDto.builder()
            .sessionToken("test-session-123")
            .language("hi")
            .build();

        HistoryStepResponseDto step1 = service.startInterview(startReq, history);
        assertEquals("आपकी मुख्य समस्या या तकलीफ क्या है?", step1.getQuestionText());
        assertEquals(List.of("दर्द", "बुखार", "खांसी / जुकाम", "पेट की समस्या", "अन्य"), step1.getQuickOptions());

        // Answer Q1 in Hindi
        HistoryAnswerRequestDto ans1 = HistoryAnswerRequestDto.builder()
            .questionKey("CHIEF_COMPLAINT")
            .patientAnswer("दर्द")
            .stepOrder(1)
            .build();

        HistoryStepResponseDto step2 = service.processAnswer(ans1, history, "hi", false);
        assertEquals("यह समस्या कब शुरू हुई थी?", step2.getQuestionText());
    }

    @Test
    void testMarathiLanguageSupport() {
        HistoryStartRequestDto startReq = HistoryStartRequestDto.builder()
            .sessionToken("test-session-123")
            .language("mr")
            .build();

        HistoryStepResponseDto step1 = service.startInterview(startReq, history);
        assertEquals("तुमची मुख्य समस्या किंवा तक्रार काय आहे?", step1.getQuestionText());
        assertEquals(List.of("वेदना / दुखणे", "ताप", "खोकला / सर्दी", "पोटाचा त्रास", "इतर"), step1.getQuickOptions());
    }
}
