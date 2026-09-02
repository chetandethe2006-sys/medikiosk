package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractedDataDto {
    private Long id;
    private Long documentId;
    private String parameterName;
    private String parameterValue;
    private String unit;
    private String referenceRange;
    private Boolean isAbnormal;
    private String abnormalDirection; // LOW, HIGH, CRITICAL
    private String physicianNote;
    private Boolean isVerified;
    private LocalDateTime extractedAt;
}
