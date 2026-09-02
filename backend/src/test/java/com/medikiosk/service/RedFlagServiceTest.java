package com.medikiosk.service;

import com.medikiosk.entity.*;
import com.medikiosk.repository.PatientSessionRepository;
import com.medikiosk.repository.RedFlagEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedFlagServiceTest {

    @Mock
    private RedFlagEventRepository redFlagEventRepository;

    @Mock
    private PatientSessionRepository patientSessionRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RedFlagService redFlagService;

    private Patient patient;
    private PatientSession session;

    @BeforeEach
    void setUp() {
        patient = Patient.builder().id(1L).fullName("Sunita Patil").age(52).gender("Female").build();
        session = PatientSession.builder().id(1L).sessionToken("test-token").patient(patient).riskLevel("NORMAL").build();
    }

    @Test
    void testDetectChestPainWithDyspneaAndDiaphoresis() {
        ClinicalHistory history = ClinicalHistory.builder()
            .patient(patient)
            .session(session)
            .chiefComplaint("Chest pain since yesterday")
            .associatedSymptoms("Breathlessness and sweating (diaphoresis)")
            .severityScale(8)
            .build();

        when(redFlagEventRepository.save(any(RedFlagEvent.class))).thenAnswer(i -> i.getArgument(0));

        List<RedFlagEvent> flags = redFlagService.evaluateSymptoms(history);

        assertFalse(flags.isEmpty());
        assertEquals("PRIORITY", flags.get(0).getSeverity());
        assertEquals("CHEST_PAIN_DYSPNEA_DIAPHORESIS", flags.get(0).getRuleKey());
        assertTrue(history.getRedFlagTriggered());
        assertEquals("PRIORITY", session.getRiskLevel());

        verify(redFlagEventRepository, times(1)).save(any(RedFlagEvent.class));
        verify(auditService, times(1)).logEvent(any(), any(), eq("RED_FLAG_DETECTED"), any(), any(), any());
    }

    @Test
    void testNoRedFlagOnMildComplaint() {
        ClinicalHistory history = ClinicalHistory.builder()
            .patient(patient)
            .session(session)
            .chiefComplaint("Mild knee ache")
            .associatedSymptoms("None")
            .severityScale(3)
            .build();

        List<RedFlagEvent> flags = redFlagService.evaluateSymptoms(history);

        assertTrue(flags.isEmpty());
        assertFalse(Boolean.TRUE.equals(history.getRedFlagTriggered()));
    }
}
