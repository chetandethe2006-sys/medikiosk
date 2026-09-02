package com.medikiosk.service;

import com.medikiosk.dto.AuthResponseDto;
import com.medikiosk.dto.LoginRequestDto;
import com.medikiosk.dto.GoogleOAuthRequestDto;
import com.medikiosk.entity.AppUser;
import com.medikiosk.entity.Patient;
import com.medikiosk.repository.AppUserRepository;
import com.medikiosk.repository.PatientRepository;
import com.medikiosk.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${medikiosk.security.google-client-id:}")
    private String googleClientId;

    @Value("${medikiosk.security.google-client-secret:}")
    private String googleClientSecret;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("============================================");
        System.out.println("Google OAuth configuration:");
        System.out.println("Client ID configured = " + (googleClientId != null && !googleClientId.isBlank()));
        System.out.println("Client Secret configured = " + (googleClientSecret != null && !googleClientSecret.isBlank()));
        System.out.println("============================================");
    }

    public AuthResponseDto authenticate(LoginRequestDto request) {
        Optional<AppUser> userOpt = appUserRepository.findByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getReferenceId());
                
                return AuthResponseDto.builder()
                        .token(token)
                        .role(user.getRole())
                        .referenceId(user.getReferenceId())
                        .name(user.getEmail())
                        .build();
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Transactional
    public AuthResponseDto authenticateGoogle(GoogleOAuthRequestDto request) {
        if (googleClientId == null || googleClientId.isEmpty()) {
            throw new IllegalArgumentException("Google OAuth is not configured on the server.");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getCredential());
            System.out.println("Google verification completed. is idToken null? " + (idToken == null));

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                
                System.out.println("Verified email present = " + (email != null));

                Optional<AppUser> userOpt = appUserRepository.findByEmail(email);
                System.out.println("Patient/User lookup result = " + (userOpt.isPresent() ? "found" : "not-found"));

                if ("DOCTOR".equalsIgnoreCase(request.getPortalType())) {
                    if (userOpt.isEmpty() || !"ROLE_DOCTOR".equals(userOpt.get().getRole())) {
                        throw new SecurityException("This Google account is not authorized for physician access.");
                    }
                    AppUser user = userOpt.get();
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getReferenceId());
                    System.out.println("JWT generation = success");
                    return AuthResponseDto.builder()
                            .token(token)
                            .role(user.getRole())
                            .referenceId(user.getReferenceId())
                            .name(name != null ? name : user.getEmail())
                            .build();

                } else if ("PATIENT".equalsIgnoreCase(request.getPortalType())) {
                    AppUser user;
                    if (userOpt.isPresent()) {
                        user = userOpt.get();
                        if (!"ROLE_PATIENT".equals(user.getRole())) {
                            throw new SecurityException("Physician accounts cannot be used as patient profiles.");
                        }
                    } else {
                        // Create a new patient
                        Patient patient = Patient.builder()
                                .fullName(name != null ? name : "Unknown Google User")
                                .age(0)
                                .gender("Unknown")
                                .preferredLanguage("en")
                                .createdAt(LocalDateTime.now())
                                .build();
                        patient = patientRepository.save(patient);

                        user = AppUser.builder()
                                .email(email)
                                .password(passwordEncoder.encode(payload.getSubject())) // Subject as dummy password
                                .role("ROLE_PATIENT")
                                .referenceId(patient.getId())
                                .build();
                        user = appUserRepository.save(user);
                        System.out.println("Created new patient account for Google login.");
                    }

                    String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getReferenceId());
                    System.out.println("JWT generation = success");
                    return AuthResponseDto.builder()
                            .token(token)
                            .role(user.getRole())
                            .referenceId(user.getReferenceId())
                            .name(name != null ? name : user.getEmail())
                            .build();
                } else {
                    throw new IllegalArgumentException("Invalid portal type.");
                }

            } else {
                throw new IllegalArgumentException("Invalid Google ID token (verify returned null).");
            }
        } catch (SecurityException | IllegalArgumentException e) {
            System.err.println("Google auth validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Google verification failed. Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Google authentication failed", e);
        }
    }

    public void registerUser(String email, String rawPassword, String role, Long referenceId) {
        if (appUserRepository.findByEmail(email).isEmpty()) {
            AppUser user = AppUser.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .referenceId(referenceId)
                    .build();
            appUserRepository.save(user);
        }
    }
}
