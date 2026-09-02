package com.medikiosk.ai;

import com.medikiosk.dto.*;
import com.medikiosk.entity.ClinicalHistory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockAIHistoryService implements AIHistoryService {

    @Override
    public HistoryStepResponseDto startInterview(HistoryStartRequestDto request, ClinicalHistory history) {
        String complaint = request.getInitialComplaint() != null ? request.getInitialComplaint().trim() : "";
        String lang = request.getLanguage() != null ? request.getLanguage() : "en";
        
        if (complaint.isEmpty()) {
            return HistoryStepResponseDto.builder()
                .questionKey("CHIEF_COMPLAINT")
                .questionText(getTranslatedText("What brings you to the hospital today?", lang))
                .speechPrompt(getTranslatedText("What brings you to the hospital today?", lang))
                .quickOptions(List.of("I have fever", "Chest pain", "Stomach pain", "Headache"))
                .inputType("CHIP_SELECT")
                .currentStepNumber(1)
                .totalEstimatedSteps(Boolean.TRUE.equals(request.getAyushMode()) ? 9 : 6)
                .completenessPercentage(10)
                .isCompleted(false)
                .build();
        }

        String category = detectCategory(complaint);
        history.setComplaintCategory(category);
        history.setChiefComplaint(complaint);

        return getStepForCategory(category, 1, lang, Boolean.TRUE.equals(request.getAyushMode()), history);
    }

    @Override
    public HistoryStepResponseDto processAnswer(HistoryAnswerRequestDto request, ClinicalHistory history, String language, Boolean ayushMode) {
        String key = request.getQuestionKey();
        String ans = request.getPatientAnswer();
        String category = history.getComplaintCategory() != null ? history.getComplaintCategory() : "GENERAL";
        int nextStep = (request.getStepOrder() != null ? request.getStepOrder() : 1) + 1;

        // Apply answer to history entity fields
        applyAnswerToHistory(key, ans, history);

        // Check if AYUSH mode adds steps
        int totalSteps = Boolean.TRUE.equals(ayushMode) ? 9 : 6;

        if (nextStep > totalSteps) {
            // Completed
            return HistoryStepResponseDto.builder()
                .sessionToken(history.getSession() != null ? history.getSession().getSessionToken() : "")
                .questionKey("COMPLETED")
                .questionText(getTranslatedText("All questions completed. Thank you! Let's proceed to document upload.", language))
                .speechPrompt(getTranslatedText("History intake complete. Please upload your previous medical documents.", language))
                .quickOptions(Collections.emptyList())
                .inputType("NONE")
                .currentStepNumber(totalSteps)
                .totalEstimatedSteps(totalSteps)
                .completenessPercentage(100)
                .isCompleted(true)
                .redFlagDetected(history.getRedFlagTriggered())
                .build();
        }

        return getStepForCategory(category, nextStep, language, Boolean.TRUE.equals(ayushMode), history);
    }

    private String detectCategory(String complaint) {
        String lower = complaint.toLowerCase();
        if (lower.contains("chest") || lower.contains("heart") || lower.contains("छाती") || lower.contains("छातीत")) {
            return "CHEST_PAIN";
        } else if (lower.contains("fever") || lower.contains("temperature") || lower.contains("बुखार") || lower.contains("ताप")) {
            return "FEVER";
        } else if (lower.contains("stomach") || lower.contains("abdomen") || lower.contains("belly") || lower.contains("पेट") || lower.contains("पोट")) {
            return "ABDOMINAL_PAIN";
        } else if (lower.contains("head") || lower.contains("सिर") || lower.contains("डोके")) {
            return "HEADACHE";
        } else if (lower.contains("cough") || lower.contains("cold") || lower.contains("खांसी") || lower.contains("खोकला")) {
            return "COUGH";
        } else if (lower.contains("sugar") || lower.contains("diabetes") || lower.contains("मधुमेह")) {
            return "DIABETES";
        }
        return "GENERAL";
    }

    private void applyAnswerToHistory(String key, String ans, ClinicalHistory history) {
        if ("ONSET".equals(key)) {
            history.setOnsetAndDuration(ans);
        } else if ("LOCATION".equals(key)) {
            history.setPainLocation(ans);
        } else if ("CHARACTER".equals(key)) {
            history.setCharacter(ans);
        } else if ("SEVERITY".equals(key)) {
            try {
                history.setSeverityScale(Integer.parseInt(ans.replaceAll("[^0-9]", "")));
            } catch (Exception ignored) {
                history.setSeverityScale(6);
            }
        } else if ("ASSOCIATED_SYMPTOMS".equals(key)) {
            history.setAssociatedSymptoms(ans);
        } else if ("AGGRAVATING_RELIEVING".equals(key)) {
            history.setAggravatingFactors(ans);
        } else if ("PAST_HISTORY".equals(key)) {
            history.setPastMedicalHistory(ans);
        } else if ("CURRENT_MEDICATIONS".equals(key)) {
            history.setCurrentMedications(ans);
        } else if ("AYUSH_PRAKRITI".equals(key)) {
            history.setPrakriti(ans);
            history.setAyushAssessed(true);
        } else if ("AYUSH_AHARA_VIHARA".equals(key)) {
            history.setAharaVihara(ans);
        } else if ("AYUSH_AGNI".equals(key)) {
            history.setAgni(ans);
        }
    }

    private HistoryStepResponseDto getStepForCategory(String category, int step, String lang, boolean ayush, ClinicalHistory history) {
        int total = ayush ? 9 : 6;
        int pct = Math.min(95, (int) Math.round(((double) step / total) * 100));

        if (ayush && step == 7) {
            return HistoryStepResponseDto.builder()
                .questionKey("AYUSH_PRAKRITI")
                .questionText(getTranslatedText("AYUSH Mode: How is your body constitution / tolerance to weather?", lang))
                .speechPrompt(getTranslatedText("How is your body constitution or climate tolerance?", lang))
                .quickOptions(List.of("Vata (Prefers Warmth, Dry Skin)", "Pitta (Sensitive to Heat, Sweating)", "Kapha (Calm, Heavy Build)", "Mixed Prakriti"))
                .inputType("CHIP_SELECT")
                .currentStepNumber(step)
                .totalEstimatedSteps(total)
                .completenessPercentage(pct)
                .isCompleted(false)
                .build();
        } else if (ayush && step == 8) {
            return HistoryStepResponseDto.builder()
                .questionKey("AYUSH_AGNI")
                .questionText(getTranslatedText("AYUSH Mode: How is your digestive power (Agni) and appetite?", lang))
                .speechPrompt(getTranslatedText("How is your appetite and digestion?", lang))
                .quickOptions(List.of("Sama (Normal & Regular)", "Vishama (Irregular)", "Tikshna (Very Intense / Hyperacidity)", "Manda (Slow / Sluggish)"))
                .inputType("CHIP_SELECT")
                .currentStepNumber(step)
                .totalEstimatedSteps(total)
                .completenessPercentage(pct)
                .isCompleted(false)
                .build();
        } else if (ayush && step == 9) {
            return HistoryStepResponseDto.builder()
                .questionKey("AYUSH_AHARA_VIHARA")
                .questionText(getTranslatedText("AYUSH Mode: What is your primary diet and sleep routine?", lang))
                .speechPrompt(getTranslatedText("Please describe your daily food and sleep routine.", lang))
                .quickOptions(List.of("Vegetarian, Regular 7h sleep", "Mixed diet, Late night dinner", "Spicy food, Irregular sleep", "Light food, Good sleep"))
                .inputType("CHIP_SELECT")
                .currentStepNumber(step)
                .totalEstimatedSteps(total)
                .completenessPercentage(pct)
                .isCompleted(false)
                .build();
        }

        // Standard Steps based on Category
        switch (category) {
            case "CHEST_PAIN":
                return getChestPainStep(step, lang, total, pct);
            case "FEVER":
                return getFeverStep(step, lang, total, pct);
            case "ABDOMINAL_PAIN":
                return getAbdominalPainStep(step, lang, total, pct);
            case "HEADACHE":
                return getHeadacheStep(step, lang, total, pct);
            case "COUGH":
                return getCoughStep(step, lang, total, pct);
            case "DIABETES":
                return getDiabetesStep(step, lang, total, pct);
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getChestPainStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("ONSET")
                    .questionText(getTranslatedText("When did this chest pain start, and did it come on suddenly or gradually?", lang))
                    .speechPrompt(getTranslatedText("When did your chest pain start?", lang))
                    .quickOptions(List.of("Started yesterday", "Started today morning", "Started 2 days ago", "Continuous for 1 week"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 2:
                return HistoryStepResponseDto.builder()
                    .questionKey("LOCATION")
                    .questionText(getTranslatedText("Where exactly do you feel the pain, and does it spread anywhere?", lang))
                    .speechPrompt(getTranslatedText("Where is the pain located, and does it spread?", lang))
                    .quickOptions(List.of("Center of chest (Substernal)", "Left side radiating to left arm", "Spreading to neck and jaw", "Right side of chest"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(2)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 3:
                return HistoryStepResponseDto.builder()
                    .questionKey("CHARACTER")
                    .questionText(getTranslatedText("What does the pain feel like?", lang))
                    .speechPrompt(getTranslatedText("What is the nature of the pain?", lang))
                    .quickOptions(List.of("Heavy pressure / crushing sensation", "Sharp / stabbing sensation", "Burning sensation", "Dull ache"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(3)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 4:
                return HistoryStepResponseDto.builder()
                    .questionKey("SEVERITY")
                    .questionText(getTranslatedText("How severe is the pain on a scale of 0 to 10? (0 = no pain, 10 = worst imaginable)", lang))
                    .speechPrompt(getTranslatedText("Please rate your pain severity from zero to ten.", lang))
                    .quickOptions(List.of("3 / 10 (Mild)", "5 / 10 (Moderate)", "7 / 10 (Severe)", "9 / 10 (Very Severe)"))
                    .inputType("SCALE_0_10")
                    .currentStepNumber(4)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 5:
                return HistoryStepResponseDto.builder()
                    .questionKey("ASSOCIATED_SYMPTOMS")
                    .questionText(getTranslatedText("Are you experiencing any of these other symptoms right now?", lang))
                    .speechPrompt(getTranslatedText("Do you have breathlessness, sweating, or dizziness?", lang))
                    .quickOptions(List.of("Breathlessness & Sweating (Diaphoresis)", "Nausea & Dizziness", "Palpitations (Fast heartbeat)", "No other symptoms"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(5)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 6:
            default:
                return HistoryStepResponseDto.builder()
                    .questionKey("PAST_HISTORY")
                    .questionText(getTranslatedText("Do you have any existing medical conditions or current medications?", lang))
                    .speechPrompt(getTranslatedText("Do you have blood pressure, diabetes, or previous heart issues?", lang))
                    .quickOptions(List.of("Hypertension (High BP) on medication", "Diabetes & High BP", "Previous heart condition / Stent", "None / First time"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(6)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
        }
    }

    private HistoryStepResponseDto getFeverStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("ONSET")
                    .questionText(getTranslatedText("How many days have you had fever, and is it continuous or intermittent?", lang))
                    .speechPrompt(getTranslatedText("How many days have you had fever?", lang))
                    .quickOptions(List.of("2 days (Continuous)", "3-4 days (Comes with chills)", "1 week on and off", "Started today"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 2:
                return HistoryStepResponseDto.builder()
                    .questionKey("ASSOCIATED_SYMPTOMS")
                    .questionText(getTranslatedText("Do you have chills, body aches, rash, or vomiting?", lang))
                    .speechPrompt(getTranslatedText("Any chills, severe body ache, or vomiting?", lang))
                    .quickOptions(List.of("Severe body ache & eye pain", "Chills & shivering", "Cough & sore throat", "Vomiting & loose motions"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(2)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 3:
                return HistoryStepResponseDto.builder()
                    .questionKey("SEVERITY")
                    .questionText(getTranslatedText("Have you measured your body temperature?", lang))
                    .speechPrompt(getTranslatedText("What was your highest recorded temperature?", lang))
                    .quickOptions(List.of("High fever (102°F - 104°F)", "Moderate fever (100°F - 101°F)", "Low grade (99°F)", "Not measured"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(3)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getAbdominalPainStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("LOCATION")
                    .questionText(getTranslatedText("Where in your stomach or abdomen is the pain located?", lang))
                    .speechPrompt(getTranslatedText("Where is your stomach pain located?", lang))
                    .quickOptions(List.of("Upper abdomen / Epigastric", "Right lower abdomen", "All over the belly", "Lower abdomen / Pelvic"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 2:
                return HistoryStepResponseDto.builder()
                    .questionKey("ASSOCIATED_SYMPTOMS")
                    .questionText(getTranslatedText("Any vomiting, fever, loose stools, or blood in stool?", lang))
                    .speechPrompt(getTranslatedText("Do you have vomiting or fever?", lang))
                    .quickOptions(List.of("Vomiting & cannot keep food down", "Loose motions (Diarrhea)", "Bloating & acidity", "Fever & severe pain"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(2)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getHeadacheStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("ONSET")
                    .questionText(getTranslatedText("Did this headache start suddenly like a thunderclap or build gradually?", lang))
                    .speechPrompt(getTranslatedText("Did the headache start suddenly or gradually?", lang))
                    .quickOptions(List.of("Sudden, extremely severe (Thunderclap)", "Gradual throbbing on one side", "Dull tightness across forehead", "Started after screen use"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getCoughStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("CHARACTER")
                    .questionText(getTranslatedText("Is your cough dry or with phlegm/sputum? Any blood?", lang))
                    .speechPrompt(getTranslatedText("Is the cough dry or productive?", lang))
                    .quickOptions(List.of("Dry cough with throat irritation", "Productive with yellowish phlegm", "Coughing up traces of blood", "Coughing mostly at night"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getDiabetesStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("PAST_HISTORY")
                    .questionText(getTranslatedText("How long have you had diabetes, and what medications are you taking?", lang))
                    .speechPrompt(getTranslatedText("How long have you been diabetic and what are your medications?", lang))
                    .quickOptions(List.of("Type 2 Diabetes (5+ years) on oral tablets", "Recent diagnosis (under 1 year)", "On Insulin injections", "Irregular medication"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            default:
                return getGeneralStep(step, lang, total, pct);
        }
    }

    private HistoryStepResponseDto getGeneralStep(int step, String lang, int total, int pct) {
        switch (step) {
            case 1:
                return HistoryStepResponseDto.builder()
                    .questionKey("ONSET")
                    .questionText(getTranslatedText("When did your symptoms start?", lang))
                    .speechPrompt(getTranslatedText("When did your symptoms begin?", lang))
                    .quickOptions(List.of("Today", "2-3 days ago", "1-2 weeks ago", "More than 1 month"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(1)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 2:
                return HistoryStepResponseDto.builder()
                    .questionKey("SEVERITY")
                    .questionText(getTranslatedText("How severe is this problem affecting your daily activities?", lang))
                    .speechPrompt(getTranslatedText("How severe is your condition?", lang))
                    .quickOptions(List.of("Mild - Can do daily work", "Moderate - Difficulty working", "Severe - Bedridden / Urgent"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(2)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
            case 3:
            default:
                return HistoryStepResponseDto.builder()
                    .questionKey("PAST_HISTORY")
                    .questionText(getTranslatedText("Do you have any existing medical conditions or current medications?", lang))
                    .speechPrompt(getTranslatedText("Do you take any regular medicines?", lang))
                    .quickOptions(List.of("High Blood Pressure", "Diabetes", "Thyroid disorder", "None / Healthy"))
                    .inputType("CHIP_SELECT")
                    .currentStepNumber(step)
                    .totalEstimatedSteps(total)
                    .completenessPercentage(pct)
                    .isCompleted(false)
                    .build();
        }
    }

    private String getTranslatedText(String english, String lang) {
        if ("hi".equalsIgnoreCase(lang)) {
            if (english.contains("chest pain start")) return "यह छाती का दर्द कब शुरू हुआ, और क्या यह अचानक हुआ या धीरे-धीरे?";
            if (english.contains("Where exactly")) return "दर्द ठीक किस जगह महसूस हो रहा है, और क्या यह कहीं और फैल रहा है?";
            if (english.contains("What does the pain feel like")) return "दर्द किस प्रकार का महसूस होता है?";
            if (english.contains("scale of 0 to 10")) return "0 से 10 के पैमाने पर दर्द की तीव्रता कितनी है?";
            if (english.contains("other symptoms right now")) return "क्या आपको इस समय सांस फूलना, पसीना आना या चक्कर महसूस हो रहा है?";
            if (english.contains("existing medical conditions")) return "क्या आपको पहले से कोई बीमारी (बीपी, शुगर) है या कोई दवा चल रही है?";
            if (english.contains("AYUSH Mode: How is your body")) return "आयुष मोड: आपकी शारीरिक प्रकृति और मौसम सहनशीलता कैसी है?";
            if (english.contains("digestive power")) return "आयुष मोड: आपकी पाचन शक्ति (अग्नि) और भूख कैसी है?";
            if (english.contains("diet and sleep")) return "आयुष मोड: आपका दैनिक आहार और नींद की दिनचर्या कैसी है?";
            if (english.contains("All questions completed")) return "सभी प्रश्न पूरे हो गए हैं। धन्यवाद! आइए पिछले दस्तावेज़ अपलोड करें।";
        } else if ("mr".equalsIgnoreCase(lang)) {
            if (english.contains("chest pain start")) return "हा छातीतला त्रास कधी सुरू झाला आणि तो अचानक सुरू झाला की हळूहळू?";
            if (english.contains("Where exactly")) return "वेदना नक्की कुठे होत आहे आणि ती डाव्या हाताकडे किंवा जबड्याकडे पसरते का?";
            if (english.contains("What does the pain feel like")) return "वेदना कशा प्रकारची जाणवते? (दाब, जडपणा किंवा टोचल्यासारखी)";
            if (english.contains("scale of 0 to 10")) return "० ते १० च्या प्रमाणात वेदनेची तीव्रता किती आहे?";
            if (english.contains("other symptoms right now")) return "सध्या धाप लागणे, खूप घाम येणे किंवा चक्कर येणे असे काही जाणवत आहे का?";
            if (english.contains("existing medical conditions")) return "तुम्हाला रक्तदाब, मधुमेह किंवा इतर कोणताही जुना आजार आहे का?";
            if (english.contains("AYUSH Mode: How is your body")) return "आयुष मोड: तुमची शारीरिक प्रकृती आणि हवामान सहनशीलता कशी आहे?";
            if (english.contains("digestive power")) return "आयुष मोड: तुमची पचनशक्ती (अग्नि) आणि भूक कशी आहे?";
            if (english.contains("diet and sleep")) return "आयुष मोड: तुमचा रोजचा आहार आणि झोपेची दिनचर्या कशी आहे?";
            if (english.contains("All questions completed")) return "सर्व प्रश्न पूर्ण झाले आहेत. धन्यवाद! आता जुने वैद्यकीय कागदपत्रे जोडूया.";
        }
        return english;
    }
}
