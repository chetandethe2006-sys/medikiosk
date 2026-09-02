package com.medikiosk.service;

import com.medikiosk.dto.FhirBundleDto;
import com.medikiosk.entity.ClinicalSummary;

public interface ABDMIntegrationService {
    FhirBundleDto exportFhirBundle(ClinicalSummary summary);
    boolean syncWithHIS(ClinicalSummary summary);
}
