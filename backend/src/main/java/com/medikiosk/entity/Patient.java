package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 32)
    private String abhaId; // e.g. 91-4567-8901-2345

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 10)
    private String gender; // Male, Female, Other

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String emergencyContact;

    @Column(length = 10)
    private String bloodGroup;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String preferredLanguage = "en"; // en, hi, mr

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
