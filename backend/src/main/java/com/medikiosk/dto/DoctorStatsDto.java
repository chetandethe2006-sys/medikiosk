package com.medikiosk.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorStatsDto {
    private Integer totalQueueToday;
    private Integer readyForConsultation;
    private Integer redFlagsCount;
    private String avgIntakeCompletionTime; // e.g. "3m 42s"
    private Integer completedToday;
    private String doctorName;
    private String opdRoom;
}
