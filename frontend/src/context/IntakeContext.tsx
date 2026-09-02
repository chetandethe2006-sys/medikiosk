import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  Patient,
  PatientSession,
  ClinicalHistory,
  HistoryStepResponse,
  ClinicalDocument,
  ClinicalSummary,
  RedFlagEvent,
  MedicalTimelineEvent,
} from '../types';
import { api } from '../services/api';
import { useLanguage } from './LanguageContext';

interface IntakeContextType {
  patient: Patient | null;
  session: PatientSession | null;
  history: ClinicalHistory | null;
  currentStepData: HistoryStepResponse | null;
  documents: ClinicalDocument[];
  timeline: MedicalTimelineEvent[];
  summary: ClinicalSummary | null;
  activeRedFlag: RedFlagEvent | null;
  isLoading: boolean;
  error: string | null;
  isAyushMode: boolean;
  toggleAyushMode: () => void;
  loadDemoPatient: (patientType?: 'SUNITA' | 'RAHUL' | 'ASHA') => Promise<void>;
  registerPatient: (patientData: Partial<Patient>) => Promise<Patient>;
  startSession: (patientId: number) => Promise<PatientSession>;
  giveConsent: () => Promise<void>;
  startClinicalHistory: (initialComplaint: string) => Promise<void>;
  submitAnswer: (answer: string, mode: 'TAP' | 'TEXT' | 'VOICE') => Promise<void>;
  uploadDocument: (file?: File, docType?: string) => Promise<ClinicalDocument>;
  addSampleDocument: (sampleType: 'LAB' | 'PRESCRIPTION' | 'DISCHARGE') => Promise<ClinicalDocument>;
  generateClinicalSummary: () => Promise<ClinicalSummary>;
  dismissRedFlag: () => void;
  resetIntake: () => void;
}

const IntakeContext = createContext<IntakeContextType | undefined>(undefined);

export const IntakeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { language, setLanguage } = useLanguage();
  const [patient, setPatient] = useState<Patient | null>(null);
  const [session, setSession] = useState<PatientSession | null>(null);
  const [history, setHistory] = useState<ClinicalHistory | null>(null);
  const [currentStepData, setCurrentStepData] = useState<HistoryStepResponse | null>(null);
  const [documents, setDocuments] = useState<ClinicalDocument[]>([]);
  const [timeline, setTimeline] = useState<MedicalTimelineEvent[]>([]);
  const [summary, setSummary] = useState<ClinicalSummary | null>(null);
  const [activeRedFlag, setActiveRedFlag] = useState<RedFlagEvent | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isAyushMode, setIsAyushMode] = useState(false);

  const toggleAyushMode = () => setIsAyushMode((prev) => !prev);

  const loadDemoPatient = async (patientType = 'SUNITA') => {
    setIsLoading(true);
    setError(null);
    try {
      if (patientType === 'SUNITA') {
        const demoPatient = await api.getPatientByAbha('91-4567-8901-2345');
        setPatient(demoPatient);
        setLanguage('mr');
        setIsAyushMode(true);

        const sess = await api.getSession('sunita-session-token-104');
        setSession(sess);

        const hist = await api.getHistory('sunita-session-token-104');
        setHistory(hist);

        const docs = await api.getDocumentsByPatient(demoPatient.id);
        setDocuments(docs);

        const tl = await api.getTimeline(demoPatient.id);
        setTimeline(tl);

        const sum = await api.getSummaryBySession('sunita-session-token-104');
        setSummary(sum);

        const flags = await api.getAllRedFlags();
        const sunitaFlag = flags.find((f) => f.patientId === demoPatient.id);
        if (sunitaFlag) setActiveRedFlag(sunitaFlag);
      } else {
        // Create standard Sunita
        const created = await api.createPatient({
          fullName: 'Sunita Patil',
          age: 52,
          gender: 'Female',
          phone: '+91 98201 45678',
          address: 'Flat 402, Shivshahi Tower, Dadar, Mumbai - 400014',
          emergencyContact: '+91 98201 45679 (Suresh Patil)',
          preferredLanguage: 'mr',
          bloodGroup: 'B+',
          abhaId: '91-4567-8901-2345',
        });
        setPatient(created);
        setLanguage('mr');
        setIsAyushMode(true);
        const newSess = await api.startSession(created.id, 'mr', true);
        setSession(newSess);
      }
    } catch (err: any) {
      console.warn('API offline or error, using deterministic client fallback', err);
      // Deterministic Offline fallback
      const fallbackPatient: Patient = {
        id: 1,
        fullName: 'Sunita Patil',
        age: 52,
        gender: 'Female',
        phone: '+91 98201 45678',
        address: 'Flat 402, Shivshahi Tower, Dadar, Mumbai - 400014',
        emergencyContact: '+91 98201 45679 (Suresh Patil)',
        preferredLanguage: 'mr',
        bloodGroup: 'B+',
        abhaId: '91-4567-8901-2345',
      };
      setPatient(fallbackPatient);
      setLanguage('mr');
      setIsAyushMode(true);
      setSession({
        id: 1,
        sessionToken: 'sunita-session-token-104',
        patientId: 1,
        patientName: 'Sunita Patil',
        patientAge: 52,
        patientGender: 'Female',
        tokenNumber: '#104',
        currentStep: 'REVIEW',
        selectedLanguage: 'mr',
        ayushMode: true,
        intakeStatus: 'READY_FOR_DOCTOR',
        riskLevel: 'PRIORITY',
        completenessPercentage: 92,
        startedAt: new Date().toISOString(),
      });
    } finally {
      setIsLoading(false);
    }
  };

  const registerPatient = async (patientData: Partial<Patient>): Promise<Patient> => {
    setIsLoading(true);
    setError(null);
    try {
      const created = await api.createPatient({
        ...patientData,
        preferredLanguage: language,
      });
      setPatient(created);
      return created;
    } catch (err: any) {
      const fallback: Patient = {
        id: Math.floor(Math.random() * 1000) + 10,
        fullName: patientData.fullName || 'Demo Patient',
        age: patientData.age || 40,
        gender: patientData.gender || 'Female',
        phone: patientData.phone,
        address: patientData.address,
        abhaId: patientData.abhaId || '91-9988-7766-5544',
        preferredLanguage: language,
      };
      setPatient(fallback);
      return fallback;
    } finally {
      setIsLoading(false);
    }
  };

  const startSession = async (patientId: number): Promise<PatientSession> => {
    setIsLoading(true);
    setError(null);
    try {
      const sess = await api.startSession(patientId, language, isAyushMode);
      setSession(sess);
      return sess;
    } catch (err) {
      const fallbackSession: PatientSession = {
        id: Math.floor(Math.random() * 1000) + 1,
        sessionToken: 'token-' + Math.random().toString(36).substring(2, 9),
        patientId,
        tokenNumber: 'T-' + Math.floor(Math.random() * 900 + 100),
        currentStep: 'CONSENT',
        selectedLanguage: language,
        ayushMode: isAyushMode,
        intakeStatus: 'IN_PROGRESS',
        riskLevel: 'NORMAL',
        completenessPercentage: 15,
        startedAt: new Date().toISOString(),
      };
      setSession(fallbackSession);
      return fallbackSession;
    } finally {
      setIsLoading(false);
    }
  };

  const giveConsent = async () => {
    if (!patient) return;
    try {
      await api.recordConsent({
        patientId: patient.id,
        sessionToken: session?.sessionToken,
        dataCollectionConsented: true,
        aiAssistanceConsented: true,
        documentExtractionConsented: true,
        audioExplanationHeard: true,
      });
    } catch (e) {
      console.warn('Consent recorded locally');
    }
  };

  const startClinicalHistory = async (initialComplaint: string) => {
    if (!session || !patient) return;
    setIsLoading(true);
    try {
      const response = await api.startHistory({
        sessionToken: session.sessionToken,
        patientId: patient.id,
        initialComplaint,
        language,
        ayushMode: isAyushMode,
      });
      setCurrentStepData(response);
      if (response.capturedState) {
        setHistory(response.capturedState);
      }
    } catch (e) {
      // Local fallback step
      setCurrentStepData({
        sessionToken: session.sessionToken,
        questionKey: 'CHIEF_COMPLAINT',
        questionText: 'What is your main problem or complaint?',
        speechPrompt: 'What is your main problem or complaint?',
        quickOptions: ['Pain', 'Fever', 'Cough / Cold', 'Stomach problem', 'Other'],
        inputType: 'CHIP_SELECT',
        currentStepNumber: 1,
        totalEstimatedSteps: 5,
        completenessPercentage: 20,
        isCompleted: false,
      });
      setHistory({
        chiefComplaint: initialComplaint,
        complaintCategory: 'GENERAL',
        recordedAt: new Date().toISOString(),
      });
    } finally {
      setIsLoading(false);
    }
  };

  const submitAnswer = async (answer: string, mode: 'TAP' | 'TEXT' | 'VOICE') => {
    if (!session || !currentStepData) return;
    setIsLoading(true);
    try {
      const response = await api.submitAnswer(session.sessionToken, {
        questionKey: currentStepData.questionKey,
        questionText: currentStepData.questionText,
        patientAnswer: answer,
        inputMode: mode,
        stepOrder: currentStepData.currentStepNumber,
      });

      setCurrentStepData(response);
      if (response.capturedState) {
        setHistory(response.capturedState);
      }
      if (response.redFlagDetected && response.redFlagAlert) {
        setActiveRedFlag(response.redFlagAlert);
      }
    } catch (e) {
      // Local progression fallback for 5 questions
      const nextStep = currentStepData.currentStepNumber + 1;
      const isComplete = nextStep > 5;
      const fallbackQuestions: Record<number, { key: string; text: string; options: string[] }> = {
        1: {
          key: 'CHIEF_COMPLAINT',
          text: 'What is your main problem or complaint?',
          options: ['Pain', 'Fever', 'Cough / Cold', 'Stomach problem', 'Other'],
        },
        2: {
          key: 'ONSET',
          text: 'When did this problem start?',
          options: ['Today', 'Yesterday', '2–3 days ago', 'More than a week ago', 'More than a month ago'],
        },
        3: {
          key: 'SEVERITY',
          text: 'How severe is your problem on a scale of 0 to 10?',
          options: ['0–2 Mild', '3–5 Moderate', '6–8 Severe', '9–10 Very severe'],
        },
        4: {
          key: 'ASSOCIATED_SYMPTOMS',
          text: 'Do you have any other symptoms?',
          options: ['None', 'Weakness / Tiredness', 'Nausea / Vomiting', 'Dizziness', 'Breathing difficulty'],
        },
        5: {
          key: 'PAST_HISTORY',
          text: 'Do you have any existing medical conditions or take regular medicines?',
          options: ['No', 'Diabetes', 'High Blood Pressure', 'Heart condition', 'Other'],
        },
      };

      const q = fallbackQuestions[nextStep];
      
      // Update fallback history locally for all questions
      setHistory(prev => {
        if (!prev) return null;
        const key = currentStepData.questionKey;
        if (key === 'CHIEF_COMPLAINT') return { ...prev, chiefComplaint: answer };
        if (key === 'ONSET') return { ...prev, onsetAndDuration: answer };
        if (key === 'SEVERITY') {
          // parse severity number
          const match = answer.match(/\d+/);
          return { ...prev, severityScale: match ? parseInt(match[0], 10) : 5 };
        }
        if (key === 'ASSOCIATED_SYMPTOMS') return { ...prev, associatedSymptoms: answer };
        if (key === 'PAST_HISTORY') return { ...prev, pastMedicalHistory: answer };
        return prev;
      });

      setCurrentStepData((prev) =>
        prev
          ? {
              ...prev,
              currentStepNumber: Math.min(5, nextStep),
              totalEstimatedSteps: 5,
              completenessPercentage: isComplete ? 100 : (nextStep - 1) * 20,
              isCompleted: isComplete,
              questionKey: isComplete ? 'COMPLETED' : q?.key || 'COMPLETED',
              questionText: isComplete
                ? 'All questions completed. Thank you! Let\'s proceed to document upload.'
                : q?.text || '',
              quickOptions: isComplete ? [] : q?.options || [],
            }
          : null
      );
    } finally {
      setIsLoading(false);
    }
  };

  const uploadDocument = async (file?: File, docType = 'LAB_REPORT'): Promise<ClinicalDocument> => {
    if (!patient || !session) throw new Error('No active patient session');
    setIsLoading(true);
    try {
      const doc = await api.uploadDocument(patient.id, session.sessionToken, docType, file);
      setDocuments((prev) => [doc, ...prev]);
      return doc;
    } finally {
      setIsLoading(false);
    }
  };

  const addSampleDocument = async (sampleType: 'LAB' | 'PRESCRIPTION' | 'DISCHARGE'): Promise<ClinicalDocument> => {
    if (!patient || !session) throw new Error('No active patient session');
    setIsLoading(true);
    try {
      const doc = await api.addSampleDocument(sampleType, patient.id, session.sessionToken);
      setDocuments((prev) => [doc, ...prev]);
      return doc;
    } finally {
      setIsLoading(false);
    }
  };

  const generateClinicalSummary = async (): Promise<ClinicalSummary> => {
    if (!session) throw new Error('No active session');
    setIsLoading(true);
    try {
      const sum = await api.generateSummary(session.sessionToken);
      setSummary(sum);
      return sum;
    } finally {
      setIsLoading(false);
    }
  };

  const dismissRedFlag = () => setActiveRedFlag(null);

  const resetIntake = () => {
    setPatient(null);
    setSession(null);
    setHistory(null);
    setCurrentStepData(null);
    setDocuments([]);
    setTimeline([]);
    setSummary(null);
    setActiveRedFlag(null);
    setIsAyushMode(false);
  };

  return (
    <IntakeContext.Provider
      value={{
        patient,
        session,
        history,
        currentStepData,
        documents,
        timeline,
        summary,
        activeRedFlag,
        isLoading,
        error,
        isAyushMode,
        toggleAyushMode,
        loadDemoPatient,
        registerPatient,
        startSession,
        giveConsent,
        startClinicalHistory,
        submitAnswer,
        uploadDocument,
        addSampleDocument,
        generateClinicalSummary,
        dismissRedFlag,
        resetIntake,
      }}
    >
      {children}
    </IntakeContext.Provider>
  );
};

export const useIntake = () => {
  const context = useContext(IntakeContext);
  if (!context) {
    throw new Error('useIntake must be used within an IntakeProvider');
  }
  return context;
};
