package com.medikiosk.ai;

import com.medikiosk.entity.*;

import java.util.List;

public interface ClinicalSummaryService {
    ClinicalSummary generateSummary(Patient patient, PatientSession session, ClinicalHistory history, List<ClinicalDocument> documents, List<ExtractedClinicalData> extractedData, List<RedFlagEvent> redFlags);
}
