package com.medikiosk.controller;

import com.medikiosk.dto.*;
import com.medikiosk.service.ClinicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HistoryController {

    private final ClinicalHistoryService historyService;

    @PostMapping("/start")
    public ResponseEntity<HistoryStepResponseDto> startHistory(@RequestBody HistoryStartRequestDto request) {
        return ResponseEntity.ok(historyService.startHistory(request));
    }

    @PostMapping("/{sessionToken}/answer")
    public ResponseEntity<HistoryStepResponseDto> submitAnswer(
            @PathVariable String sessionToken,
            @RequestBody HistoryAnswerRequestDto request) {
        return ResponseEntity.ok(historyService.submitAnswer(sessionToken, request));
    }

    @GetMapping("/{sessionToken}")
    public ResponseEntity<ClinicalHistoryDto> getHistoryBySession(@PathVariable String sessionToken) {
        return ResponseEntity.ok(historyService.getHistoryBySessionToken(sessionToken));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ClinicalHistoryDto> getHistoryByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(historyService.getHistoryByPatientId(patientId));
    }
}
