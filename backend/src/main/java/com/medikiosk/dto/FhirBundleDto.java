package com.medikiosk.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FhirBundleDto {
    private String resourceType; // "Bundle"
    private String type; // "document"
    private String id;
    private String timestamp;
    private String identifier;
    private List<Map<String, Object>> entry;
    private String syncStatus; // "MOCK_SYNC_SUCCESS"
    private String abdmHealthRecordNumber;
    private String message;
}
