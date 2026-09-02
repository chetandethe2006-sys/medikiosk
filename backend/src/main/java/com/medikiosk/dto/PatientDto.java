package com.medikiosk.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDto {
    private Long id;
    private String abhaId;
    @NotBlank(message = "Patient name is required")
    private String fullName;
    @NotNull(message = "Age is required")
    private Integer age;
    @NotBlank(message = "Gender is required")
    private String gender;
    private String phone;
    private String address;
    private String emergencyContact;
    private String bloodGroup;
    private String preferredLanguage;
    private LocalDateTime createdAt;
}
