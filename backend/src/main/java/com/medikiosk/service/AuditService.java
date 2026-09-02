package com.medikiosk.service;

import com.medikiosk.dto.AuditEventDto;
import com.medikiosk.entity.AuditEvent;
import com.medikiosk.entity.Patient;
import com.medikiosk.entity.PatientSession;
import com.medikiosk.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public AuditEvent logEvent(Patient patient, PatientSession session, String eventType, String description, String performedBy, String metadataJson) {
        AuditEvent event = AuditEvent.builder()
            .patient(patient)
            .session(session)
            .eventType(eventType)
            .description(description)
            .performedBy(performedBy != null ? performedBy : "PATIENT_KIOSK")
            .metadataJson(metadataJson)
            .timestamp(LocalDateTime.now())
            .build();

        return auditEventRepository.save(event);
    }

    public List<AuditEventDto> getAuditLogsForPatient(Long patientId) {
        return auditEventRepository.findByPatientIdOrderByTimestampDesc(patientId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public List<AuditEventDto> getRecentAuditLogs() {
        return auditEventRepository.findTop50ByOrderByTimestampDesc()
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public AuditEventDto mapToDto(AuditEvent event) {
        return AuditEventDto.builder()
            .id(event.getId())
            .patientId(event.getPatient() != null ? event.getPatient().getId() : null)
            .eventType(event.getEventType())
            .description(event.getDescription())
            .performedBy(event.getPerformedBy())
            .metadataJson(event.getMetadataJson())
            .timestamp(event.getTimestamp())
            .build();
    }
}
