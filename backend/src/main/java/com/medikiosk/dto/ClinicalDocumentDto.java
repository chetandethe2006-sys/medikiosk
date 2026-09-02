package com.medikiosk.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalDocumentDto {
    private Long id;
    private Long patientId;
    private String sessionToken;
    private String fileName;
    private String documentType;
    private String fileExtension;
    private Long fileSizeBytes;
    private String processingStatus;
    private String rawOcrText;
    private LocalDateTime documentDate;
    private LocalDateTime uploadedAt;
    private List<ExtractedDataDto> extractedParameters;
}
