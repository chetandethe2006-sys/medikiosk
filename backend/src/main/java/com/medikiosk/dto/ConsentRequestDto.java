package com.medikiosk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentRequestDto {
    private Long patientId;
    private String sessionToken;
    private Boolean dataCollectionConsented;
    private Boolean aiAssistanceConsented;
    private Boolean documentExtractionConsented;
    private Boolean audioExplanationHeard;
    private String policyVersion;
    private String kioskId;
}
