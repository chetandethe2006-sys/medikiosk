package com.medikiosk.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_PATIENT, ROLE_DOCTOR

    private Long referenceId; // Patient ID or Doctor ID

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
