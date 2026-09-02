package com.medikiosk.ai;

import com.medikiosk.dto.ClinicalDocumentDto;
import com.medikiosk.entity.ClinicalDocument;
import com.medikiosk.entity.ExtractedClinicalData;

import java.util.List;

public interface DocumentExtractionService {
    List<ExtractedClinicalData> extractStructuredData(ClinicalDocument document);
}
