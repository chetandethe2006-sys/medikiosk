package com.medikiosk.ai;

import com.medikiosk.dto.*;
import com.medikiosk.entity.ClinicalHistory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockAIHistoryService implements AIHistoryService {

    private static final int TOTAL_QUESTIONS = 5;

    @Override
    public HistoryStepResponseDto startInterview(HistoryStartRequestDto request, ClinicalHistory history) {
        String lang = request.getLanguage() != null ? request.getLanguage() : "en";
        // Always start with Question 1
        return getQuestionStep(1, lang, history);
    }

    @Override
    public HistoryStepResponseDto processAnswer(HistoryAnswerRequestDto request, ClinicalHistory history, String language, Boolean ayushMode) {
        String key = request.getQuestionKey();
        String ans = request.getPatientAnswer();
        String lang = language != null ? language : "en";
        int currentStep = request.getStepOrder() != null ? request.getStepOrder() : 1;
        int nextStep = currentStep + 1;

        // Apply answer to history entity fields
        applyAnswerToHistory(key, ans, history);

        if (nextStep > TOTAL_QUESTIONS) {
            // Completed after exactly 5 questions
            return HistoryStepResponseDto.builder()
                .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                .questionKey("COMPLETED")
                .questionText(getTranslatedText("All questions completed. Thank you! Let's proceed to document upload.", lang))
                .speechPrompt(getTranslatedSpeechPrompt("History intake complete. Please upload your previous medical documents.", lang))
                .quickOptions(Collections.emptyList())
                .inputType("NONE")
                .currentStepNumber(TOTAL_QUESTIONS)
                .totalEstimatedSteps(TOTAL_QUESTIONS)
                .completenessPercentage(100)
                .isCompleted(true)
                .redFlagDetected(history.getRedFlagTriggered())
                .build();
        }

        return getQuestionStep(nextStep, lang, history);
    }

    private HistoryStepResponseDto getQuestionStep(int step, String lang, ClinicalHistory history) {
        int completeness = (step - 1) * 20; // 0, 20, 40, 60, 80
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                    .questionKey("CHIEF_COMPLAINT")
                    .questionText(getQuestion1Text(lang))
                    .speechPrompt(getQuestion1Speech(lang))
                    .quickOptions(getQuestion1Options(lang))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(TOTAL_QUESTIONS)
                    .completenessPercentage(completeness)
                    .isCompleted(false)
                    .redFlagDetected(history.getRedFlagTriggered())
                    .build();

            case 2:
                return HistoryStepResponseDto.builder()
                    .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                    .questionKey("ONSET")
                    .questionText(getQuestion2Text(lang))
                    .speechPrompt(getQuestion2Speech(lang))
                    .quickOptions(getQuestion2Options(lang))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(2)
                    .totalEstimatedSteps(TOTAL_QUESTIONS)
                    .completenessPercentage(completeness)
                    .isCompleted(false)
                    .redFlagDetected(history.getRedFlagTriggered())
                    .build();

            case 3:
                return HistoryStepResponseDto.builder()
                    .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                    .questionKey("SEVERITY")
                    .questionText(getQuestion3Text(lang))
                    .speechPrompt(getQuestion3Speech(lang))
                    .quickOptions(getQuestion3Options(lang))
                    .inputType("SCALE_0_10")
                    .currentStepNumber(3)
                    .totalEstimatedSteps(TOTAL_QUESTIONS)
                    .completenessPercentage(completeness)
                    .isCompleted(false)
                    .redFlagDetected(history.getRedFlagTriggered())
                    .build();

            case 4:
                return HistoryStepResponseDto.builder()
                    .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                    .questionKey("ASSOCIATED_SYMPTOMS")
                    .questionText(getQuestion4Text(lang))
                    .speechPrompt(getQuestion4Speech(lang))
                    .quickOptions(getQuestion4Options(lang))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(4)
                    .totalEstimatedSteps(TOTAL_QUESTIONS)
                    .completenessPercentage(completeness)
                    .isCompleted(false)
                    .redFlagDetected(history.getRedFlagTriggered())
                    .build();

            case 5:
            default:
                return HistoryStepResponseDto.builder()
                    .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                    .questionKey("PAST_HISTORY")
                    .questionText(getQuestion5Text(lang))
                    .speechPrompt(getQuestion5Speech(lang))
                    .quickOptions(getQuestion5Options(lang))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(5)
                    .totalEstimatedSteps(TOTAL_QUESTIONS)
                    .completenessPercentage(completeness)
                    .isCompleted(false)
                    .redFlagDetected(history.getRedFlagTriggered())
                    .build();
        }
    }

    private void applyAnswerToHistory(String key, String ans, ClinicalHistory history) {
        if (ans == null) return;

        if ("CHIEF_COMPLAINT".equals(key)) {
            history.setChiefComplaint(ans);
            history.setComplaintCategory(detectCategory(ans));
        } else if ("ONSET".equals(key)) {
            history.setOnsetAndDuration(ans);
        } else if ("SEVERITY".equals(key)) {
            history.setSeverityScale(parseSeverity(ans));
        } else if ("ASSOCIATED_SYMPTOMS".equals(key)) {
            history.setAssociatedSymptoms(ans);
        } else if ("PAST_HISTORY".equals(key)) {
            history.setPastMedicalHistory(ans);
            if (!ans.equalsIgnoreCase("No") && !ans.equalsIgnoreCase("None") && !ans.equalsIgnoreCase("नहीं") && !ans.equalsIgnoreCase("नाही")) {
                history.setCurrentMedications(ans);
            }
        }
    }

    private int parseSeverity(String ans) {
        if (ans == null || ans.trim().isEmpty()) return 5;
        String trimmed = ans.trim();
        if (trimmed.startsWith("0") || trimmed.contains("0–2") || trimmed.contains("0-2") || trimmed.toLowerCase().contains("mild") || trimmed.contains("हल्का") || trimmed.contains("सौम्य")) {
            return 2;
        } else if (trimmed.startsWith("3") || trimmed.contains("3–5") || trimmed.contains("3-5") || trimmed.toLowerCase().contains("moderate") || trimmed.contains("मध्यम")) {
            return 4;
        } else if (trimmed.startsWith("6") || trimmed.contains("6–8") || trimmed.contains("6-8") || trimmed.toLowerCase().contains("severe") && !trimmed.toLowerCase().contains("very") || trimmed.contains("गंभीर") || trimmed.contains("तीव्र")) {
            return 7;
        } else if (trimmed.startsWith("9") || trimmed.contains("9–10") || trimmed.contains("9-10") || trimmed.toLowerCase().contains("very severe") || trimmed.contains("बहुत गंभीर") || trimmed.contains("अति तीव्र")) {
            return 9;
        }
        try {
            String digits = trimmed.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                int val = Integer.parseInt(digits);
                if (val > 10 && val <= 100) val = val / 10;
                return Math.min(10, Math.max(0, val));
            }
        } catch (Exception ignored) {
        }
        return 5;
    }

    private String detectCategory(String complaint) {
        if (complaint == null) return "GENERAL";
        String lower = complaint.toLowerCase();
        if (lower.contains("chest") || lower.contains("heart") || lower.contains("छाती") || lower.contains("छातीत") || lower.contains("हृदय")) {
            return "CHEST_PAIN";
        } else if (lower.contains("fever") || lower.contains("temperature") || lower.contains("बुखार") || lower.contains("ताप")) {
            return "FEVER";
        } else if (lower.contains("stomach") || lower.contains("abdomen") || lower.contains("belly") || lower.contains("पेट") || lower.contains("पोट")) {
            return "ABDOMINAL_PAIN";
        } else if (lower.contains("head") || lower.contains("सिर") || lower.contains("डोके")) {
            return "HEADACHE";
        } else if (lower.contains("cough") || lower.contains("cold") || lower.contains("खांसी") || lower.contains("खोकला") || lower.contains("सर्दी")) {
            return "COUGH";
        } else if (lower.contains("sugar") || lower.contains("diabetes") || lower.contains("मधुमेह")) {
            return "DIABETES";
        } else if (lower.contains("pain") || lower.contains("दर्द") || lower.contains("दुखणे")) {
            return "PAIN";
        }
        return "GENERAL";
    }

    // Question 1
    private String getQuestion1Text(String lang) {
        if ("hi".equalsIgnoreCase(lang)) return "आपकी मुख्य समस्या या तकलीफ क्या है?";
        if ("mr".equalsIgnoreCase(lang)) return "तुमची मुख्य समस्या किंवा तक्रार काय आहे?";
        return "What is your main problem or complaint?";
    }
    private String getQuestion1Speech(String lang) {
        return getQuestion1Text(lang);
    }
    private List<String> getQuestion1Options(String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            return List.of("दर्द", "बुखार", "खांसी / जुकाम", "पेट की समस्या", "अन्य");
        } else if ("mr".equalsIgnoreCase(lang)) {
            return List.of("वेदना / दुखणे", "ताप", "खोकला / सर्दी", "पोटाचा त्रास", "इतर");
        }
        return List.of("Pain", "Fever", "Cough / Cold", "Stomach problem", "Other");
    }

    // Question 2
    private String getQuestion2Text(String lang) {
        if ("hi".equalsIgnoreCase(lang)) return "यह समस्या कब शुरू हुई थी?";
        if ("mr".equalsIgnoreCase(lang)) return "हा त्रास कधी सुरू झाला?";
        return "When did this problem start?";
    }
    private String getQuestion2Speech(String lang) {
        return getQuestion2Text(lang);
    }
    private List<String> getQuestion2Options(String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            return List.of("आज", "कल", "२–३ दिन पहले", "एक सप्ताह से अधिक", "एक महीने से अधिक");
        } else if ("mr".equalsIgnoreCase(lang)) {
            return List.of("आज", "काल", "२–३ दिवसांपूर्वी", "एका आठवड्यापेक्षा जास्त", "एका महिन्यापेक्षा जास्त");
        }
        return List.of("Today", "Yesterday", "2–3 days ago", "More than a week ago", "More than a month ago");
    }

    // Question 3
    private String getQuestion3Text(String lang) {
        if ("hi".equalsIgnoreCase(lang)) return "0 से 10 के पैमाने पर आपकी समस्या कितनी गंभीर है?";
        if ("mr".equalsIgnoreCase(lang)) return "० ते १० च्या प्रमाणात तुमचा त्रास किती तीव्र आहे?";
        return "How severe is your problem on a scale of 0 to 10?";
    }
    private String getQuestion3Speech(String lang) {
        return getQuestion3Text(lang);
    }
    private List<String> getQuestion3Options(String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            return List.of("0–2 हल्का", "3–5 मध्यम", "6–8 गंभीर", "9–10 बहुत गंभीर");
        } else if ("mr".equalsIgnoreCase(lang)) {
            return List.of("०–२ सौम्य", "३–५ मध्यम", "६–८ तीव्र", "९–१० अति तीव्र");
        }
        return List.of("0–2 Mild", "3–5 Moderate", "6–8 Severe", "9–10 Very severe");
    }

    // Question 4
    private String getQuestion4Text(String lang) {
        if ("hi".equalsIgnoreCase(lang)) return "क्या आपको कोई अन्य लक्षण महसूस हो रहे हैं?";
        if ("mr".equalsIgnoreCase(lang)) return "तुम्हाला इतर काही लक्षणे जाणवत आहेत का?";
        return "Do you have any other symptoms?";
    }
    private String getQuestion4Speech(String lang) {
        return getQuestion4Text(lang);
    }
    private List<String> getQuestion4Options(String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            return List.of("कोई नहीं", "कमजोरी / थकान", "मतली / उल्टी", "चक्कर आना", "सांस लेने में कठिनाई");
        } else if ("mr".equalsIgnoreCase(lang)) {
            return List.of("काहीही नाही", "अशक्तपणा / थकवा", "मळमळ / उलटी", "चक्कर येणे", "श्वास घेण्यास त्रास");
        }
        return List.of("None", "Weakness / Tiredness", "Nausea / Vomiting", "Dizziness", "Breathing difficulty");
    }

    // Question 5
    private String getQuestion5Text(String lang) {
        if ("hi".equalsIgnoreCase(lang)) return "क्या आपको पहले से कोई बीमारी है या कोई नियमित दवा लेते हैं?";
        if ("mr".equalsIgnoreCase(lang)) return "तुम्हाला आधीपासून काही आजार आहे का किंवा नियमित औषधे घेता का?";
        return "Do you have any existing medical conditions or take regular medicines?";
    }
    private String getQuestion5Speech(String lang) {
        return getQuestion5Text(lang);
    }
    private List<String> getQuestion5Options(String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            return List.of("नहीं", "मधुमेह (डायबिटीज)", "उच्च रक्तचाप (हाई बीपी)", "हृदय रोग", "अन्य");
        } else if ("mr".equalsIgnoreCase(lang)) {
            return List.of("नाही", "मधुमेह (डायबिटीज)", "उच्च रक्तदाब (हाय बीपी)", "हृदयविकार", "इतर");
        }
        return List.of("No", "Diabetes", "High Blood Pressure", "Heart condition", "Other");
    }

    private String getTranslatedText(String english, String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            if (english.contains("All questions completed")) return "सभी प्रश्न पूरे हो गए हैं। धन्यवाद! आइए पिछले दस्तावेज़ अपलोड करें।";
        } else if ("mr".equalsIgnoreCase(lang)) {
            if (english.contains("All questions completed")) return "सर्व प्रश्न पूर्ण झाले आहेत. धन्यवाद! आता जुने वैद्यकीय कागदपत्रे जोडूया.";
        }
        return english;
    }

    private String getTranslatedSpeechPrompt(String english, String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            if (english.contains("History intake complete")) return "स्वास्थ्य इतिहास पूरा हो गया है। कृपया अपने पिछले चिकित्सा दस्तावेज अपलोड करें।";
        } else if ("mr".equalsIgnoreCase(lang)) {
            if (english.contains("History intake complete")) return "आरोग्य इतिहास पूर्ण झाला आहे. कृपया तुमची मागील वैद्यकीय कागदपत्रे अपलोड करा.";
        }
        return english;
    }
}
