package com.medikiosk.controller;

import com.medikiosk.dto.AuthResponseDto;
import com.medikiosk.dto.LoginRequestDto;
import com.medikiosk.service.AuthenticationService;
import com.medikiosk.dto.GoogleOAuthRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleOAuthRequestDto request) {
        try {
            AuthResponseDto response = authService.authenticateGoogle(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Configuration missing or invalid token
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (SecurityException e) {
            // Unauthorized access (e.g. not a doctor)
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Google Auth Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }
}
