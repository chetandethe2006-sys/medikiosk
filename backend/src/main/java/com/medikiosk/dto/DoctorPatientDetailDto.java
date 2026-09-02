package com.medikiosk.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPatientDetailDto {
    private PatientDto patient;
    private PatientSessionDto session;
    private ClinicalHistoryDto history;
    private ClinicalSummaryDto summary;
    private List<ClinicalDocumentDto> documents;
    private List<MedicalTimelineEventDto> timeline;
    private List<RedFlagEventDto> redFlags;
    private List<AuditEventDto> auditLogs;
}
