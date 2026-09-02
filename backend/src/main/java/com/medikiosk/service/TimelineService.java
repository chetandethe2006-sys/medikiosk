package com.medikiosk.service;

import com.medikiosk.dto.MedicalTimelineEventDto;
import com.medikiosk.entity.MedicalTimelineEvent;
import com.medikiosk.repository.MedicalTimelineEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final MedicalTimelineEventRepository timelineRepository;

    public List<MedicalTimelineEventDto> getTimelineForPatient(Long patientId) {
        return timelineRepository.findByPatientIdOrderByEventDateDesc(patientId)
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    public MedicalTimelineEventDto mapToDto(MedicalTimelineEvent e) {
        return MedicalTimelineEventDto.builder()
            .id(e.getId())
            .patientId(e.getPatient() != null ? e.getPatient().getId() : null)
            .eventDate(e.getEventDate())
            .title(e.getTitle())
            .category(e.getCategory())
            .summary(e.getSummary())
            .medications(e.getMedications())
            .facilityOrDoctor(e.getFacilityOrDoctor())
            .documentRef(e.getDocumentRef())
            .createdAt(e.getCreatedAt())
            .build();
    }
}
