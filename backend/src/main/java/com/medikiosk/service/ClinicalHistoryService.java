package com.medikiosk.service;

import com.medikiosk.ai.AIHistoryService;
import com.medikiosk.dto.*;
import com.medikiosk.entity.*;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicalHistoryService {

    private final ClinicalHistoryRepository historyRepository;
    private final HistoryAnswerRepository answerRepository;
    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final AIHistoryService aiHistoryService;
    private final RedFlagService redFlagService;
    private final AuditService auditService;

    @Transactional
    public HistoryStepResponseDto startHistory(HistoryStartRequestDto request) {
        PatientSession session = sessionRepository.findBySessionToken(request.getSessionToken())
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + request.getSessionToken()));

        Patient patient = session.getPatient();

        // Check if history already exists for session, else create
        ClinicalHistory history = historyRepository.findBySessionId(session.getId())
            .orElseGet(() -> ClinicalHistory.builder()
                .patient(patient)
                .session(session)
                .chiefComplaint(request.getInitialComplaint())
                .recordedAt(LocalDateTime.now())
                .build());

        if (request.getAyushMode() != null) {
            session.setAyushMode(request.getAyushMode());
            sessionRepository.save(session);
        }

        history = historyRepository.save(history);

        HistoryStepResponseDto response = aiHistoryService.startInterview(request, history);
        historyRepository.save(history);

        auditService.logEvent(
            patient,
            session,
            "HISTORY_STARTED",
            "Clinical history interview started for complaint: " + history.getChiefComplaint(),
            "PATIENT_KIOSK",
            "{\"category\":\"" + history.getComplaintCategory() + "\"}"
        );

        response.setCapturedState(mapToDto(history));
        return response;
    }

    @Transactional
    public HistoryStepResponseDto submitAnswer(String sessionToken, HistoryAnswerRequestDto request) {
        PatientSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionToken));

        ClinicalHistory history = historyRepository.findBySessionId(session.getId())
            .orElseThrow(() -> new ResourceNotFoundException("History record not initialized for session: " + sessionToken));

        // Save individual answer log
        HistoryAnswer answer = HistoryAnswer.builder()
            .history(history)
            .questionKey(request.getQuestionKey())
            .questionText(request.getQuestionText())
            .patientAnswer(request.getPatientAnswer())
            .inputMode(request.getInputMode() != null ? request.getInputMode() : "TAP")
            .stepOrder(request.getStepOrder() != null ? request.getStepOrder() : 1)
            .answeredAt(LocalDateTime.now())
            .build();
        answerRepository.save(answer);

        // Process answer via AI dialogue engine
        HistoryStepResponseDto response = aiHistoryService.processAnswer(
            request,
            history,
            session.getSelectedLanguage(),
            session.getAyushMode()
        );

        // Check for Red Flags in real-time
        List<RedFlagEvent> redFlags = redFlagService.evaluateSymptoms(history);
        if (!redFlags.isEmpty()) {
            response.setRedFlagDetected(true);
            response.setRedFlagAlert(redFlagService.mapToDto(redFlags.get(0)));
        }

        // Update session completeness
        if (response.getCompletenessPercentage() != null) {
            session.setCompletenessPercentage(response.getCompletenessPercentage());
            if (response.getIsCompleted()) {
                session.setCurrentStep("DOCUMENTS");
            }
            sessionRepository.save(session);
        }

        historyRepository.save(history);
        response.setCapturedState(mapToDto(history));
        return response;
    }

    public ClinicalHistoryDto getHistoryBySessionToken(String sessionToken) {
        PatientSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionToken));

        ClinicalHistory history = historyRepository.findBySessionId(session.getId())
            .orElseThrow(() -> new ResourceNotFoundException("History not found for session: " + sessionToken));

        return mapToDto(history);
    }

    public ClinicalHistoryDto getHistoryByPatientId(Long patientId) {
        List<ClinicalHistory> list = historyRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("History not found for patient: " + patientId);
        }
        return mapToDto(list.get(0));
    }

    public ClinicalHistoryDto mapToDto(ClinicalHistory h) {
        List<ClinicalHistoryDto.HistoryAnswerDto> answerDtos = answerRepository.findByHistoryIdOrderByStepOrderAsc(h.getId())
            .stream()
            .map(a -> ClinicalHistoryDto.HistoryAnswerDto.builder()
                .questionKey(a.getQuestionKey())
                .questionText(a.getQuestionText())
                .patientAnswer(a.getPatientAnswer())
                .inputMode(a.getInputMode())
                .stepOrder(a.getStepOrder())
                .build())
            .collect(Collectors.toList());

        return ClinicalHistoryDto.builder()
            .id(h.getId())
            .patientId(h.getPatient() != null ? h.getPatient().getId() : null)
            .sessionToken(h.getSession() != null ? h.getSession().getSessionToken() : null)
            .chiefComplaint(h.getChiefComplaint())
            .complaintCategory(h.getComplaintCategory())
            .onsetAndDuration(h.getOnsetAndDuration())
            .painLocation(h.getPainLocation())
            .character(h.getCharacter())
            .severityScale(h.getSeverityScale())
            .radiation(h.getRadiation())
            .aggravatingFactors(h.getAggravatingFactors())
            .relievingFactors(h.getRelievingFactors())
            .associatedSymptoms(h.getAssociatedSymptoms())
            .pastMedicalHistory(h.getPastMedicalHistory())
            .pastSurgicalHistory(h.getPastSurgicalHistory())
            .currentMedications(h.getCurrentMedications())
            .drugAllergies(h.getDrugAllergies())
            .familyHistory(h.getFamilyHistory())
            .personalHistory(h.getPersonalHistory())
            .ayushAssessed(h.getAyushAssessed())
            .prakriti(h.getPrakriti())
            .vikriti(h.getVikriti())
            .agni(h.getAgni())
            .koshtha(h.getKoshtha())
            .aharaVihara(h.getAharaVihara())
            .nidana(h.getNidana())
            .redFlagTriggered(h.getRedFlagTriggered())
            .answers(answerDtos)
            .recordedAt(h.getRecordedAt())
            .build();
    }
}
