package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventDto {
    private Long id;
    private Long patientId;
    private String eventType;
    private String description;
    private String performedBy;
    private String metadataJson;
    private LocalDateTime timestamp;
}
