package com.medikiosk.service;

import com.medikiosk.dto.*;
import com.medikiosk.entity.*;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final ClinicalHistoryRepository historyRepository;
    private final ClinicalSummaryRepository summaryRepository;
    private final ClinicalDocumentRepository documentRepository;
    private final ExtractedClinicalDataRepository extractedDataRepository;
    private final MedicalTimelineEventRepository timelineRepository;
    private final RedFlagEventRepository redFlagRepository;
    private final AuditEventRepository auditRepository;
    private final PatientService patientService;
    private final ClinicalHistoryService historyService;
    private final SummaryService summaryService;
    private final DocumentService documentService;
    private final TimelineService timelineService;
    private final RedFlagService redFlagService;
    private final AuditService auditService;

    public List<DoctorQueueItemDto> getQueue() {
        List<PatientSession> sessions = sessionRepository.findAllByOrderByStartedAtDesc();
        List<DoctorQueueItemDto> queue = new ArrayList<>();

        for (PatientSession s : sessions) {
            Patient p = s.getPatient();
            if (p == null) continue;

            ClinicalHistory h = historyRepository.findBySessionId(s.getId()).orElse(null);
            ClinicalSummary sum = summaryRepository.findBySessionId(s.getId()).orElse(null);
            List<ClinicalDocument> docs = documentRepository.findBySessionIdOrderByUploadedAtDesc(s.getId());
            boolean hasRedFlag = Boolean.TRUE.equals(s.getRiskLevel() != null && "PRIORITY".equalsIgnoreCase(s.getRiskLevel()))
                || (h != null && Boolean.TRUE.equals(h.getRedFlagTriggered()));

            queue.add(DoctorQueueItemDto.builder()
                .sessionId(s.getId())
                .patientId(p.getId())
                .tokenNumber(s.getTokenNumber())
                .patientName(p.getFullName())
                .age(p.getAge())
                .gender(p.getGender())
                .abhaId(p.getAbhaId())
                .chiefComplaint(h != null && h.getChiefComplaint() != null ? h.getChiefComplaint() : "Intake pending")
                .intakeStatus(s.getIntakeStatus())
                .riskLevel(hasRedFlag ? "PRIORITY" : s.getRiskLevel())
                .redFlagTriggered(hasRedFlag)
                .documentCount(docs.size())
                .ayushMode(s.getAyushMode())
                .completenessPercentage(s.getCompletenessPercentage())
                .startedAt(s.getStartedAt())
                .summaryId(sum != null ? sum.getId() : null)
                .build());
        }

        return queue;
    }

    public DoctorStatsDto getDoctorStats() {
        List<PatientSession> sessions = sessionRepository.findAll();
        int total = sessions.size();
        int ready = (int) sessions.stream().filter(s -> "READY_FOR_DOCTOR".equalsIgnoreCase(s.getIntakeStatus())).count();
        int completed = (int) sessions.stream().filter(s -> "COMPLETED".equalsIgnoreCase(s.getIntakeStatus())).count();
        int redFlags = (int) redFlagRepository.findByTriageStatusOrderByDetectedAtDesc("AWAITING_REVIEW").size();
        long totalDurationMinutes = 0;
        int completedCount = 0;
        for (PatientSession s : sessions) {
            if ("COMPLETED".equalsIgnoreCase(s.getIntakeStatus()) && s.getStartedAt() != null) {
                long duration = java.time.Duration.between(s.getStartedAt(), java.time.LocalDateTime.now()).toMinutes();
                // for prototype purpose just a mock calculation if real duration is unavailable
                totalDurationMinutes += (duration > 0 && duration < 60) ? duration : 5;
                completedCount++;
            }
        }
        String avgTime = completedCount > 0 ? (totalDurationMinutes / completedCount) + "m" : "0m";

        return DoctorStatsDto.builder()
            .totalQueueToday(total)
            .readyForConsultation(ready)
            .redFlagsCount(redFlags)
            .avgIntakeCompletionTime(avgTime)
            .completedToday(completed)
            .doctorName("Dr. Rajesh Sharma, MD")
            .opdRoom("OPD Room 104 — AIIA")
            .build();
    }

    @Transactional(readOnly = true)
    public DoctorPatientDetailDto getDoctorPatientDetail(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        List<PatientSession> sessions = sessionRepository.findByPatientIdOrderByStartedAtDesc(patientId);
        PatientSession session = !sessions.isEmpty() ? sessions.get(0) : null;

        ClinicalHistory history = null;
        if (session != null) {
            history = historyRepository.findBySessionId(session.getId()).orElse(null);
        }
        if (history == null) {
            List<ClinicalHistory> histories = historyRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
            if (!histories.isEmpty()) history = histories.get(0);
        }

        ClinicalSummary summary = summaryRepository.findTopByPatientIdOrderByGeneratedAtDesc(patientId).orElse(null);
        List<ClinicalDocumentDto> documents = documentService.getDocumentsByPatient(patientId);
        List<MedicalTimelineEventDto> timeline = timelineService.getTimelineForPatient(patientId);
        List<RedFlagEventDto> redFlags = redFlagService.getRedFlagsByPatient(patientId);
        List<AuditEventDto> auditLogs = auditService.getAuditLogsForPatient(patientId);

        return DoctorPatientDetailDto.builder()
            .patient(patientService.mapToDto(patient))
            .session(session != null ? patientService.mapSessionToDto(session) : null)
            .history(history != null ? historyService.mapToDto(history) : null)
            .summary(summary != null ? summaryService.mapToDto(summary) : null)
            .documents(documents)
            .timeline(timeline)
            .redFlags(redFlags)
            .auditLogs(auditLogs)
            .build();
    }
}
