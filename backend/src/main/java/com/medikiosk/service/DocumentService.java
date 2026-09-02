package com.medikiosk.service;

import com.medikiosk.ai.DocumentExtractionService;
import com.medikiosk.dto.ClinicalDocumentDto;
import com.medikiosk.dto.ExtractedDataDto;
import com.medikiosk.entity.*;
import com.medikiosk.exception.ResourceNotFoundException;
import com.medikiosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ClinicalDocumentRepository documentRepository;
    private final ExtractedClinicalDataRepository extractedDataRepository;
    private final PatientRepository patientRepository;
    private final PatientSessionRepository sessionRepository;
    private final MedicalTimelineEventRepository timelineRepository;
    private final DocumentExtractionService extractionService;
    private final AuditService auditService;

    @Transactional
    public ClinicalDocumentDto uploadDocument(Long patientId, String sessionToken, String documentType, MultipartFile file) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        PatientSession session = null;
        if (sessionToken != null && !sessionToken.isBlank()) {
            session = sessionRepository.findBySessionToken(sessionToken).orElse(null);
        }

        String originalFilename = file != null && file.getOriginalFilename() != null ? file.getOriginalFilename() : "Lab_Report_12_July.pdf";
        String ext = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "pdf";
        long size = file != null ? file.getSize() : 245000L;

        ClinicalDocument document = ClinicalDocument.builder()
            .patient(patient)
            .session(session)
            .fileName(originalFilename)
            .documentType(documentType != null ? documentType : "LAB_REPORT")
            .fileExtension(ext)
            .fileSizeBytes(size)
            .processingStatus("PROCESSING")
            .documentDate(LocalDateTime.now().minusDays(15))
            .uploadedAt(LocalDateTime.now())
            .build();

        document = documentRepository.save(document);

        // Perform OCR / AI Structured Extraction
        List<ExtractedClinicalData> extracted = extractionService.extractStructuredData(document);
        for (ExtractedClinicalData data : extracted) {
            extractedDataRepository.save(data);
        }

        document.setProcessingStatus("EXTRACTED");
        document = documentRepository.save(document);

        // Automatically create a medical timeline event for this document
        createTimelineEventForDoc(patient, document, extracted);

        auditService.logEvent(
            patient,
            session,
            "DOCUMENT_PROCESSED",
            "OCR extraction completed for document: " + originalFilename + " (" + extracted.size() + " clinical parameters)",
            "AI_OCR_ENGINE",
            "{\"documentId\":" + document.getId() + ",\"type\":\"" + document.getDocumentType() + "\"}"
        );

        return mapToDto(document, extracted);
    }

    @Transactional
    public ClinicalDocumentDto addSampleDocument(Long patientId, String sessionToken, String sampleType) {
        String fileName;
        String docType;
        if ("LAB".equalsIgnoreCase(sampleType)) {
            fileName = "Biochemical_Blood_Panel_12_Jul.pdf";
            docType = "LAB_REPORT";
        } else if ("PRESCRIPTION".equalsIgnoreCase(sampleType)) {
            fileName = "Cardiology_OPD_Prescription.jpg";
            docType = "PRESCRIPTION";
        } else {
            fileName = "Hospital_Discharge_Summary_2025.pdf";
            docType = "DISCHARGE_SUMMARY";
        }

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));

        PatientSession session = null;
        if (sessionToken != null && !sessionToken.isBlank()) {
            session = sessionRepository.findBySessionToken(sessionToken).orElse(null);
        }

        ClinicalDocument document = ClinicalDocument.builder()
            .patient(patient)
            .session(session)
            .fileName(fileName)
            .documentType(docType)
            .fileExtension(fileName.substring(fileName.lastIndexOf(".") + 1))
            .fileSizeBytes(184000L)
            .processingStatus("PROCESSING")
            .documentDate(LocalDateTime.now().minusDays(20))
            .uploadedAt(LocalDateTime.now())
            .build();

        document = documentRepository.save(document);

        List<ExtractedClinicalData> extracted = extractionService.extractStructuredData(document);
        for (ExtractedClinicalData data : extracted) {
            extractedDataRepository.save(data);
        }

        document.setProcessingStatus("EXTRACTED");
        document = documentRepository.save(document);

        createTimelineEventForDoc(patient, document, extracted);

        auditService.logEvent(
            patient,
            session,
            "DOCUMENT_PROCESSED",
            "Sample document added and digitized: " + fileName,
            "SYSTEM_DEMO",
            "{\"documentId\":" + document.getId() + "}"
        );

        return mapToDto(document, extracted);
    }

    public List<ClinicalDocumentDto> getDocumentsByPatient(Long patientId) {
        return documentRepository.findByPatientIdOrderByUploadedAtDesc(patientId)
            .stream()
            .map(doc -> {
                List<ExtractedClinicalData> data = extractedDataRepository.findByDocumentId(doc.getId());
                return mapToDto(doc, data);
            })
            .collect(Collectors.toList());
    }

    public List<ClinicalDocumentDto> getDocumentsBySession(String sessionToken) {
        PatientSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionToken));

        return documentRepository.findBySessionIdOrderByUploadedAtDesc(session.getId())
            .stream()
            .map(doc -> {
                List<ExtractedClinicalData> data = extractedDataRepository.findByDocumentId(doc.getId());
                return mapToDto(doc, data);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public ExtractedDataDto updateExtractedParameter(Long id, String newValue, String note) {
        ExtractedClinicalData data = extractedDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Extracted data not found: " + id));

        if (newValue != null) data.setParameterValue(newValue);
        if (note != null) data.setPhysicianNote(note);
        data.setIsVerified(true);

        ExtractedClinicalData saved = extractedDataRepository.save(data);
        return mapDataToDto(saved);
    }

    private void createTimelineEventForDoc(Patient patient, ClinicalDocument doc, List<ExtractedClinicalData> extracted) {
        String summary = extracted.stream()
            .map(d -> d.getParameterName() + ": " + d.getParameterValue() + " " + (d.getUnit() != null ? d.getUnit() : ""))
            .collect(Collectors.joining(", "));

        MedicalTimelineEvent event = MedicalTimelineEvent.builder()
            .patient(patient)
            .eventDate(LocalDate.now().minusDays(15))
            .title(doc.getDocumentType().replace("_", " ") + " (" + doc.getFileName() + ")")
            .category(doc.getDocumentType())
            .summary(summary.length() > 250 ? summary.substring(0, 247) + "..." : summary)
            .medications("Amlodipine 5mg, Metformin 500mg")
            .facilityOrDoctor("AIIA New Delhi - OPD")
            .documentRef(doc.getFileName())
            .createdAt(LocalDateTime.now())
            .build();

        timelineRepository.save(event);
    }

    public ClinicalDocumentDto mapToDto(ClinicalDocument doc, List<ExtractedClinicalData> extracted) {
        List<ExtractedDataDto> paramDtos = extracted.stream()
            .map(this::mapDataToDto)
            .collect(Collectors.toList());

        return ClinicalDocumentDto.builder()
            .id(doc.getId())
            .patientId(doc.getPatient() != null ? doc.getPatient().getId() : null)
            .sessionToken(doc.getSession() != null ? doc.getSession().getSessionToken() : null)
            .fileName(doc.getFileName())
            .documentType(doc.getDocumentType())
            .fileExtension(doc.getFileExtension())
            .fileSizeBytes(doc.getFileSizeBytes())
            .processingStatus(doc.getProcessingStatus())
            .rawOcrText(doc.getRawOcrText())
            .documentDate(doc.getDocumentDate())
            .uploadedAt(doc.getUploadedAt())
            .extractedParameters(paramDtos)
            .build();
    }

    public ExtractedDataDto mapDataToDto(ExtractedClinicalData d) {
        return ExtractedDataDto.builder()
            .id(d.getId())
            .documentId(d.getDocument() != null ? d.getDocument().getId() : null)
            .parameterName(d.getParameterName())
            .parameterValue(d.getParameterValue())
            .unit(d.getUnit())
            .referenceRange(d.getReferenceRange())
            .isAbnormal(d.getIsAbnormal())
            .abnormalDirection(d.getAbnormalDirection())
            .physicianNote(d.getPhysicianNote())
            .isVerified(d.getIsVerified())
            .extractedAt(d.getExtractedAt())
            .build();
    }
}
