package com.medikiosk.ai;

import com.medikiosk.entity.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MockClinicalSummaryService implements ClinicalSummaryService {

    @Override
    public ClinicalSummary generateSummary(
            Patient patient,
            PatientSession session,
            ClinicalHistory history,
            List<ClinicalDocument> documents,
            List<ExtractedClinicalData> extractedData,
            List<RedFlagEvent> redFlags) {

        String chiefComplaint = history != null && history.getChiefComplaint() != null 
            ? history.getChiefComplaint() 
            : "Acute symptom presentation";

        String onset = history != null && history.getOnsetAndDuration() != null 
            ? history.getOnsetAndDuration() 
            : "Recent onset";

        String associated = history != null && history.getAssociatedSymptoms() != null 
            ? history.getAssociatedSymptoms() 
            : "None reported";

        String pastMed = history != null && history.getPastMedicalHistory() != null 
            ? history.getPastMedicalHistory() 
            : "Known Hypertension (5 years), T2DM (2 years)";

        String currentMeds = history != null && history.getCurrentMedications() != null 
            ? history.getCurrentMedications() 
            : "Tab Amlodipine 5mg OD, Tab Metformin 500mg BD";

        // Red flag summary
        boolean hasPriorityRedFlag = redFlags != null && !redFlags.isEmpty();
        String redFlagSummary = hasPriorityRedFlag
            ? "YES – Priority Review (" + redFlags.get(0).getTitle() + ")"
            : "NO – Standard Triage (No immediate critical flags detected)";

        // Recent labs summary
        String recentLabs = extractedData != null && !extractedData.isEmpty()
            ? extractedData.stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsAbnormal()))
                .map(d -> d.getParameterName() + ": " + d.getParameterValue() + " " + (d.getUnit() != null ? d.getUnit() : "") + " (" + d.getAbnormalDirection() + ")")
                .collect(Collectors.joining("; "))
            : "Hb 10.2 g/dL (Low), Fasting Glucose 138 mg/dL (High)";

        // Full Detailed HPI
        StringBuilder hpiBuilder = new StringBuilder();
        hpiBuilder.append("Patient ").append(patient != null ? patient.getFullName() : "Patient")
            .append(", ").append(patient != null ? patient.getAge() : "52").append("y/")
            .append(patient != null ? patient.getGender() : "F")
            .append(" presents via self-service MediKiosk intake with complaints of ")
            .append(chiefComplaint).append(" (").append(onset).append(").\n")
            .append("Character: ").append(history != null && history.getCharacter() != null ? history.getCharacter() : "Heavy crushing pressure").append(".\n")
            .append("Location & Radiation: ").append(history != null && history.getPainLocation() != null ? history.getPainLocation() : "Substernal area").append(".\n")
            .append("Severity Score: ").append(history != null && history.getSeverityScale() != null ? history.getSeverityScale() : "7").append("/10.\n")
            .append("Associated Symptoms: ").append(associated).append(".\n")
            .append("Aggravating / Relieving Factors: ").append(history != null && history.getAggravatingFactors() != null ? history.getAggravatingFactors() : "Aggravated on walking; partial relief on rest").append(".");

        // AYUSH Assessment summary if enabled
        String ayushSummary = "";
        if (Boolean.TRUE.equals(session != null ? session.getAyushMode() : false) || (history != null && Boolean.TRUE.equals(history.getAyushAssessed()))) {
            ayushSummary = "AYUSH Ayurvedic Assessment:\n"
                + "• Prakriti: " + (history != null && history.getPrakriti() != null ? history.getPrakriti() : "Pitta-Vata") + "\n"
                + "• Agni (Digestive Fire): " + (history != null && history.getAgni() != null ? history.getAgni() : "Vishama Agni (Irregular digestion)") + "\n"
                + "• Ahara & Vihara: " + (history != null && history.getAharaVihara() != null ? history.getAharaVihara() : "Vegetarian diet, irregular sleep, mental stress") + "\n"
                + "• Ayurvedic Differential (Nidana): Hridroga Lakshana / Vata-Pitta Dushti.";
        }

        return ClinicalSummary.builder()
            .patient(patient)
            .session(session)
            .history(history)
            // 30-Second Doctor Quick View
            .quickViewChiefComplaint(chiefComplaint + " (" + onset + ")")
            .quickViewKeySymptoms(associated + (history != null && history.getPainLocation() != null ? " • " + history.getPainLocation() : ""))
            .quickViewRedFlagSummary(redFlagSummary)
            .quickViewPastHistory(pastMed)
            .quickViewCurrentMeds(currentMeds)
            .quickViewRecentLabs(recentLabs)
            // Full Detailed Draft
            .chiefComplaintText(chiefComplaint)
            .historyOfPresentIllness(hpiBuilder.toString())
            .pastMedicalHistory(pastMed)
            .pastSurgicalHistory(history != null && history.getPastSurgicalHistory() != null ? history.getPastSurgicalHistory() : "Appendectomy in 2018 (uneventful)")
            .drugAndAllergyHistory(history != null && history.getDrugAllergies() != null ? history.getDrugAllergies() : "NKDA (No Known Drug Allergies)")
            .familyAndPersonalHistory(history != null && history.getFamilyHistory() != null ? history.getFamilyHistory() : "Father had ischemic heart disease at age 58. Non-smoker, non-alcoholic.")
            .reviewOfSystems("Cardiovascular: Positive for chest pressure and palpitations. Respiratory: Positive for exertional breathlessness. GI: Mild epigastric burning. Neurological: Negative for syncopal attacks.")
            .previousInvestigations("Recent Blood Panel (12 Jul 2026): Hb 10.2 g/dL (Mild Anemia), Fasting Blood Sugar 138 mg/dL, Creatinine 1.1 mg/dL. 2D Echo (2025): LVEF 55% normal LV systolic function.")
            .currentMedications(currentMeds)
            .potentialRedFlags(hasPriorityRedFlag ? "PRIORITY ALERT: Chest pain combined with diaphoresis and exertional breathlessness. Urgent ECG and Troponin I indicated." : "No critical red flags detected during self-intake.")
            .ayushAssessmentSummary(ayushSummary)
            .physicianNotes("Physician verification pending consultation.")
            .status("AI_DRAFT")
            .safetyNotice("AI Generated Draft — Physician Verification Required before clinical decisions")
            .hisSynced(false)
            .fhirResourceGenerated(false)
            .generatedAt(LocalDateTime.now())
            .build();
    }
}
