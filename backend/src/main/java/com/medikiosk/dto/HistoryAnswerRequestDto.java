package com.medikiosk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryAnswerRequestDto {
    private String questionKey;
    private String questionText;
    private String patientAnswer;
    private String inputMode; // TAP, TEXT, VOICE
    private Integer stepOrder;
}
