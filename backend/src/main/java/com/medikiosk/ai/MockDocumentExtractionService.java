package com.medikiosk.ai;

import com.medikiosk.entity.ClinicalDocument;
import com.medikiosk.entity.ExtractedClinicalData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockDocumentExtractionService implements DocumentExtractionService {

    @Override
    public List<ExtractedClinicalData> extractStructuredData(ClinicalDocument document) {
        List<ExtractedClinicalData> dataList = new ArrayList<>();
        String type = document.getDocumentType() != null ? document.getDocumentType().toUpperCase() : "LAB_REPORT";

        if ("Lab_Report_12_July.pdf".equalsIgnoreCase(document.getFileName()) || "Biochemical_Blood_Panel_12_Jul.pdf".equalsIgnoreCase(document.getFileName())) {
            // Seeded Lab Report
            dataList.add(ExtractedClinicalData.builder()
                .document(document)
                .parameterName("Hemoglobin (Hb)")
                .parameterValue("10.2")
                .unit("g/dL")
                .referenceRange("12.0 - 15.5")
                .isAbnormal(true)
                .abnormalDirection("LOW")
                .physicianNote("Mild anemia")
                .isVerified(false)
                .build());
            dataList.add(ExtractedClinicalData.builder()
                .document(document)
                .parameterName("Fasting Blood Glucose")
                .parameterValue("138")
                .unit("mg/dL")
                .referenceRange("70 - 99")
                .isAbnormal(true)
                .abnormalDirection("HIGH")
                .physicianNote("Impaired fasting glycaemia")
                .isVerified(false)
                .build());
        } else if ("Prescription_Cardiology_AIIA.jpg".equalsIgnoreCase(document.getFileName()) || "Cardiology_OPD_Prescription.jpg".equalsIgnoreCase(document.getFileName())) {
            // Seeded Prescription
            dataList.add(ExtractedClinicalData.builder()
                .document(document)
                .parameterName("Medication: Tab Amlodipine")
                .parameterValue("5 mg")
                .unit("OD (Once daily)")
                .referenceRange("Oral")
                .isAbnormal(false)
                .abnormalDirection("ACTIVE_MED")
                .physicianNote("Antihypertensive")
                .isVerified(false)
                .build());
        } else if ("Hospital_Discharge_Summary_2025.pdf".equalsIgnoreCase(document.getFileName())) {
             dataList.add(ExtractedClinicalData.builder()
                .document(document)
                .parameterName("Discharge Diagnosis")
                .parameterValue("Hypertensive Urgency, Resolved")
                .unit("ICD-10 I10")
                .referenceRange("Resolved")
                .isAbnormal(false)
                .abnormalDirection("RESOLVED")
                .physicianNote("Admitted for 48h, discharged stable")
                .isVerified(false)
                .build());
        }
        // For any actual user upload, we do NOT fabricate data
        return dataList;
    }
}
