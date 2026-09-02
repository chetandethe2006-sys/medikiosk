package com.medikiosk.service;

import com.medikiosk.dto.FhirBundleDto;
import com.medikiosk.entity.ClinicalSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockABDMIntegrationService implements ABDMIntegrationService {

    private final FHIRMapper fhirMapper;

    @Override
    public FhirBundleDto exportFhirBundle(ClinicalSummary summary) {
        return fhirMapper.createFhirBundle(summary);
    }

    @Override
    public boolean syncWithHIS(ClinicalSummary summary) {
        return true;
    }
}
