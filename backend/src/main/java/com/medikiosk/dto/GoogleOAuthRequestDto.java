package com.medikiosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthRequestDto {
    private String credential; // The Google JWT ID Token
    private String portalType; // "PATIENT" or "DOCTOR"
}
