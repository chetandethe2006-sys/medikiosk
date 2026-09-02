package com.medikiosk.service;

import com.medikiosk.dto.FhirBundleDto;
import com.medikiosk.entity.ClinicalSummary;
import com.medikiosk.entity.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class FHIRMapper {

    public FhirBundleDto createFhirBundle(ClinicalSummary summary) {
        Patient patient = summary.getPatient();
        String bundleId = "medikiosk-fhir-" + UUID.randomUUID().toString().substring(0, 8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        List<Map<String, Object>> entries = new ArrayList<>();

        // Patient Resource Entry
        Map<String, Object> patientResource = new LinkedHashMap<>();
        patientResource.put("resourceType", "Patient");
        patientResource.put("id", patient != null ? "pat-" + patient.getId() : "pat-01");
        patientResource.put("identifier", List.of(Map.of("system", "https://healthid.ndhm.gov.in", "value", patient != null && patient.getAbhaId() != null ? patient.getAbhaId() : "91-4567-8901-2345")));
        patientResource.put("name", List.of(Map.of("text", patient != null ? patient.getFullName() : "Demo Patient")));
        patientResource.put("gender", patient != null && patient.getGender() != null ? patient.getGender().toLowerCase() : "female");
        entries.add(Map.of("fullUrl", "urn:uuid:" + UUID.randomUUID(), "resource", patientResource));

        // Composition / Clinical Note Entry
        Map<String, Object> compositionResource = new LinkedHashMap<>();
        compositionResource.put("resourceType", "Composition");
        compositionResource.put("status", "final");
        compositionResource.put("type", Map.of("coding", List.of(Map.of("system", "http://loinc.org", "code", "11488-4", "display", "Consultation note"))));
        compositionResource.put("title", "MediKiosk Structured Clinical History Record");
        compositionResource.put("date", timestamp);
        compositionResource.put("section", List.of(
            Map.of("title", "Chief Complaint", "text", Map.of("status", "generated", "div", summary.getChiefComplaintText() != null ? summary.getChiefComplaintText() : "")),
            Map.of("title", "History of Present Illness", "text", Map.of("status", "generated", "div", summary.getHistoryOfPresentIllness() != null ? summary.getHistoryOfPresentIllness() : "")),
            Map.of("title", "Active Medications", "text", Map.of("status", "generated", "div", summary.getCurrentMedications() != null ? summary.getCurrentMedications() : "")),
            Map.of("title", "Potential Red Flags", "text", Map.of("status", "generated", "div", summary.getPotentialRedFlags() != null ? summary.getPotentialRedFlags() : ""))
        ));
        entries.add(Map.of("fullUrl", "urn:uuid:" + UUID.randomUUID(), "resource", compositionResource));

        return FhirBundleDto.builder()
            .resourceType("Bundle")
            .type("document")
            .id(bundleId)
            .timestamp(timestamp)
            .identifier("ABDM-RECORD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
            .entry(entries)
            .syncStatus("MOCK_SYNC_SUCCESS")
            .abdmHealthRecordNumber("ABDM/AIIA/2026/" + (1000 + (int)(Math.random() * 9000)))
            .message("FHIR R4 DiagnosticReport & Composition Document Bundle compiled successfully. (Demo/Mock Integration Ready)")
            .build();
    }
}
