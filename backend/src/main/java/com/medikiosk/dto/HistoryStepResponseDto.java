package com.medikiosk.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryStepResponseDto {
    private String sessionToken;
    private String questionKey;
    private String questionText;
    private String speechPrompt; // Audio pronunciation prompt
    private List<String> quickOptions; // Touch chips
    private String inputType; // CHIP_SELECT, SCALE_0_10, TEXT_OR_VOICE, YES_NO
    private Integer currentStepNumber;
    private Integer totalEstimatedSteps;
    private Integer completenessPercentage;
    private Boolean isCompleted;
    private Boolean redFlagDetected;
    private RedFlagEventDto redFlagAlert;
    private ClinicalHistoryDto capturedState;
}
