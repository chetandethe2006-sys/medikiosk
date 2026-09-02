package com.medikiosk.controller;

import com.medikiosk.dto.MedicalTimelineEventDto;
import com.medikiosk.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalTimelineEventDto>> getPatientTimeline(@PathVariable Long patientId) {
        return ResponseEntity.ok(timelineService.getTimelineForPatient(patientId));
    }
}
