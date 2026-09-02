package com.medikiosk.ai;

import com.medikiosk.dto.HistoryAnswerRequestDto;
import com.medikiosk.dto.HistoryStartRequestDto;
import com.medikiosk.dto.HistoryStepResponseDto;
import com.medikiosk.entity.ClinicalHistory;

public interface AIHistoryService {
    HistoryStepResponseDto startInterview(HistoryStartRequestDto request, ClinicalHistory history);
    HistoryStepResponseDto processAnswer(HistoryAnswerRequestDto request, ClinicalHistory history, String language, Boolean ayushMode);
}
