package com.medikiosk.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryStartRequestDto {
    private String sessionToken;
    private Long patientId;
    private String initialComplaint; // e.g. "Chest pain", "Fever", "Abdominal pain"
    private String language; // en, hi, mr
    private Boolean ayushMode;
}
