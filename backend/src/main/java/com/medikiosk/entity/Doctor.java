package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName; // e.g. "Dr. Rajesh Sharma, MD"

    @Column(nullable = false, length = 100)
    private String department; // "Internal Medicine / Kayachikitsa"

    @Column(length = 50)
    private String opdRoom; // "OPD Room 104"

    @Column(unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
