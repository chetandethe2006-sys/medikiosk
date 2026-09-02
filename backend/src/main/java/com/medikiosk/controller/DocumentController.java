package com.medikiosk.controller;

import com.medikiosk.dto.ClinicalDocumentDto;
import com.medikiosk.dto.ExtractedDataDto;
import com.medikiosk.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ClinicalDocumentDto> uploadDocument(
            @RequestParam("patientId") Long patientId,
            @RequestParam(value = "sessionToken", required = false) String sessionToken,
            @RequestParam(value = "documentType", defaultValue = "LAB_REPORT") String documentType,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        ClinicalDocumentDto dto = documentService.uploadDocument(patientId, sessionToken, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/sample/{sampleType}")
    public ResponseEntity<ClinicalDocumentDto> addSampleDocument(
            @PathVariable String sampleType,
            @RequestBody Map<String, Object> body) {
        Long patientId = Long.valueOf(body.get("patientId").toString());
        String sessionToken = body.get("sessionToken") != null ? body.get("sessionToken").toString() : null;

        ClinicalDocumentDto dto = documentService.addSampleDocument(patientId, sessionToken, sampleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClinicalDocumentDto>> getDocumentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(documentService.getDocumentsByPatient(patientId));
    }

    @GetMapping("/session/{sessionToken}")
    public ResponseEntity<List<ClinicalDocumentDto>> getDocumentsBySession(@PathVariable String sessionToken) {
        return ResponseEntity.ok(documentService.getDocumentsBySession(sessionToken));
    }

    @PutMapping("/parameter/{id}")
    public ResponseEntity<ExtractedDataDto> updateExtractedParameter(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newValue = body.get("parameterValue");
        String note = body.get("physicianNote");
        return ResponseEntity.ok(documentService.updateExtractedParameter(id, newValue, note));
    }
}
