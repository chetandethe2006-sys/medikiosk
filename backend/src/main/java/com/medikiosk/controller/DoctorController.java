package com.medikiosk.controller;

import com.medikiosk.dto.DoctorPatientDetailDto;
import com.medikiosk.dto.DoctorQueueItemDto;
import com.medikiosk.dto.DoctorStatsDto;
import com.medikiosk.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/queue")
    public ResponseEntity<List<DoctorQueueItemDto>> getQueue() {
        return ResponseEntity.ok(doctorService.getQueue());
    }

    @GetMapping("/stats")
    public ResponseEntity<DoctorStatsDto> getStats() {
        return ResponseEntity.ok(doctorService.getDoctorStats());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<DoctorPatientDetailDto> getPatientDetail(@PathVariable Long patientId) {
        return ResponseEntity.ok(doctorService.getDoctorPatientDetail(patientId));
    }
}
