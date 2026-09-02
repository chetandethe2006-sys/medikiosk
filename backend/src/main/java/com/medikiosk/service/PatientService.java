package com.medikiosk.service;

import com.medikiosk.dto.PatientDto;
import com.medikiosk.dto.PatientSessionDto;
import com.medikiosk.entity.Patient;
import com.medikiosk.entity.PatientSession;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.PatientRepository;
import com.medikiosk.repository.PatientSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final AuditService auditService;

    @Transactional
    public PatientDto createOrGetPatient(PatientDto dto) {
        Patient patient;
        if (dto.getAbhaId() != null && !dto.getAbhaId().isBlank()) {
            patient = patientRepository.findByAbhaId(dto.getAbhaId())
                .orElseGet(() -> mapToEntity(dto));
        } else {
            patient = mapToEntity(dto);
        }

        // Update fields if existing
        patient.setFullName(dto.getFullName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setPreferredLanguage(dto.getPreferredLanguage() != null ? dto.getPreferredLanguage() : "en");

        Patient saved = patientRepository.save(patient);
        return mapToDto(saved);
    }

    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
        return mapToDto(patient);
    }

    public PatientDto getPatientByAbha(String abhaId) {
        Patient patient = patientRepository.findByAbhaId(abhaId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found for ABHA ID: " + abhaId));
        return mapToDto(patient);
    }

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public PatientSessionDto startSession(Long patientId, String language, Boolean ayushMode) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        String tokenNum = "T-" + (100 + (int)(Math.random() * 900));
        String sessionToken = UUID.randomUUID().toString();

        PatientSession session = PatientSession.builder()
            .sessionToken(sessionToken)
            .patient(patient)
            .tokenNumber(tokenNum)
            .currentStep("CONSENT")
            .selectedLanguage(language != null ? language : patient.getPreferredLanguage())
            .ayushMode(Boolean.TRUE.equals(ayushMode))
            .intakeStatus("IN_PROGRESS")
            .riskLevel("NORMAL")
            .completenessPercentage(10)
            .startedAt(LocalDateTime.now())
            .build();

        PatientSession saved = sessionRepository.save(session);

        auditService.logEvent(
            patient,
            saved,
            "SESSION_STARTED",
            "Kiosk intake session initiated for " + patient.getFullName() + " with Token " + tokenNum,
            "PATIENT_KIOSK",
            "{\"tokenNumber\":\"" + tokenNum + "\"}"
        );

        return mapSessionToDto(saved);
    }

    public PatientSessionDto getSessionByToken(String token) {
        PatientSession session = sessionRepository.findBySessionToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + token));
        return mapSessionToDto(session);
    }

    @Transactional
    public PatientSessionDto updateSessionProgress(String token, String step, Integer completeness) {
        PatientSession session = sessionRepository.findBySessionToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + token));

        if (step != null) session.setCurrentStep(step);
        if (completeness != null) session.setCompletenessPercentage(completeness);

        if ("COMPLETED".equals(step) || "REVIEW".equals(step)) {
            session.setIntakeStatus("READY_FOR_DOCTOR");
            session.setCompletedAt(LocalDateTime.now());
        }

        PatientSession saved = sessionRepository.save(session);
        return mapSessionToDto(saved);
    }

    public PatientDto mapToDto(Patient p) {
        return PatientDto.builder()
            .id(p.getId())
            .abhaId(p.getAbhaId())
            .fullName(p.getFullName())
            .age(p.getAge())
            .gender(p.getGender())
            .phone(p.getPhone())
            .address(p.getAddress())
            .emergencyContact(p.getEmergencyContact())
            .bloodGroup(p.getBloodGroup())
            .preferredLanguage(p.getPreferredLanguage())
            .createdAt(p.getCreatedAt())
            .build();
    }

    public Patient mapToEntity(PatientDto dto) {
        return Patient.builder()
            .id(dto.getId())
            .abhaId(dto.getAbhaId())
            .fullName(dto.getFullName())
            .age(dto.getAge())
            .gender(dto.getGender())
            .phone(dto.getPhone())
            .address(dto.getAddress())
            .emergencyContact(dto.getEmergencyContact())
            .bloodGroup(dto.getBloodGroup())
            .preferredLanguage(dto.getPreferredLanguage() != null ? dto.getPreferredLanguage() : "en")
            .createdAt(LocalDateTime.now())
            .build();
    }

    public PatientSessionDto mapSessionToDto(PatientSession s) {
        return PatientSessionDto.builder()
            .id(s.getId())
            .sessionToken(s.getSessionToken())
            .patientId(s.getPatient() != null ? s.getPatient().getId() : null)
            .patientName(s.getPatient() != null ? s.getPatient().getFullName() : null)
            .patientAge(s.getPatient() != null ? s.getPatient().getAge() : null)
            .patientGender(s.getPatient() != null ? s.getPatient().getGender() : null)
            .tokenNumber(s.getTokenNumber())
            .currentStep(s.getCurrentStep())
            .selectedLanguage(s.getSelectedLanguage())
            .ayushMode(s.getAyushMode())
            .intakeStatus(s.getIntakeStatus())
            .riskLevel(s.getRiskLevel())
            .completenessPercentage(s.getCompletenessPercentage())
            .startedAt(s.getStartedAt())
            .completedAt(s.getCompletedAt())
            .build();
    }
}
