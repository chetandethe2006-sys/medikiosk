export type LanguageCode = 'en' | 'hi' | 'mr';

export type RiskLevel = 'NORMAL' | 'REVIEW' | 'PRIORITY';
export type IntakeStatus = 'IN_PROGRESS' | 'READY_FOR_DOCTOR' | 'IN_CONSULTATION' | 'COMPLETED';

export interface Patient {
  id: number;
  abhaId?: string;
  fullName: string;
  age: number;
  gender: string;
  phone?: string;
  address?: string;
  emergencyContact?: string;
  bloodGroup?: string;
  preferredLanguage?: LanguageCode;
  createdAt?: string;
}

export interface PatientSession {
  id: number;
  sessionToken: string;
  patientId: number;
  patientName?: string;
  patientAge?: number;
  patientGender?: string;
  tokenNumber: string;
  currentStep: string;
  selectedLanguage: LanguageCode;
  ayushMode: boolean;
  intakeStatus: IntakeStatus;
  riskLevel: RiskLevel;
  completenessPercentage: number;
  startedAt: string;
  completedAt?: string;
}

export interface HistoryAnswer {
  questionKey: string;
  questionText: string;
  patientAnswer: string;
  inputMode: 'TAP' | 'TEXT' | 'VOICE';
  stepOrder: number;
}

export interface ClinicalHistory {
  id?: number;
  patientId?: number;
  sessionToken?: string;
  chiefComplaint: string;
  complaintCategory?: string;
  onsetAndDuration?: string;
  painLocation?: string;
  character?: string;
  severityScale?: number;
  radiation?: string;
  aggravatingFactors?: string;
  relievingFactors?: string;
  associatedSymptoms?: string;
  pastMedicalHistory?: string;
  pastSurgicalHistory?: string;
  currentMedications?: string;
  drugAllergies?: string;
  familyHistory?: string;
  personalHistory?: string;
  ayushAssessed?: boolean;
  prakriti?: string;
  vikriti?: string;
  agni?: string;
  koshtha?: string;
  aharaVihara?: string;
  nidana?: string;
  redFlagTriggered?: boolean;
  answers?: HistoryAnswer[];
  recordedAt?: string;
}

export interface HistoryStepResponse {
  sessionToken: string;
  questionKey: string;
  questionText: string;
  speechPrompt: string;
  quickOptions: string[];
  inputType: 'CHIP_SELECT' | 'SCALE_0_10' | 'TEXT_OR_VOICE' | 'YES_NO' | 'NONE';
  currentStepNumber: number;
  totalEstimatedSteps: number;
  completenessPercentage: number;
  isCompleted: boolean;
  redFlagDetected?: boolean;
  redFlagAlert?: RedFlagEvent;
  capturedState?: ClinicalHistory;
}

export interface ExtractedData {
  id: number;
  documentId: number;
  parameterName: string;
  parameterValue: string;
  unit?: string;
  referenceRange?: string;
  isAbnormal: boolean;
  abnormalDirection?: string;
  physicianNote?: string;
  isVerified: boolean;
  extractedAt?: string;
}

export interface ClinicalDocument {
  id: number;
  patientId: number;
  sessionToken?: string;
  fileName: string;
  documentType: 'PRESCRIPTION' | 'LAB_REPORT' | 'DISCHARGE_SUMMARY' | 'IMAGING_REPORT';
  fileExtension: string;
  fileSizeBytes?: number;
  processingStatus: 'UPLOADING' | 'PROCESSING' | 'EXTRACTED' | 'NEEDS_REVIEW' | 'FAILED';
  rawOcrText?: string;
  documentDate?: string;
  uploadedAt: string;
  extractedParameters?: ExtractedData[];
}

export interface MedicalTimelineEvent {
  id: number;
  patientId: number;
  eventDate: string;
  title: string;
  category: string;
  summary: string;
  medications?: string;
  facilityOrDoctor?: string;
  documentRef?: string;
  createdAt?: string;
}

export interface RedFlagEvent {
  id: number;
  patientId: number;
  patientName?: string;
  patientAge?: number;
  patientGender?: string;
  sessionToken?: string;
  tokenNumber?: string;
  ruleKey: string;
  severity: RiskLevel;
  title: string;
  symptomsReported: string;
  clinicalRecommendation: string;
  triageStatus: 'AWAITING_REVIEW' | 'ACKNOWLEDGED' | 'ESCALATED' | 'RESOLVED';
  acknowledgedByDoctor?: string;
  acknowledgedAt?: string;
  detectedAt: string;
}

export interface ClinicalSummary {
  id: number;
  patientId: number;
  patientName?: string;
  patientAge?: number;
  patientGender?: string;
  abhaId?: string;
  tokenNumber?: string;
  sessionToken?: string;
  quickViewChiefComplaint?: string;
  quickViewKeySymptoms?: string;
  quickViewRedFlagSummary?: string;
  quickViewPastHistory?: string;
  quickViewCurrentMeds?: string;
  quickViewRecentLabs?: string;
  chiefComplaintText?: string;
  historyOfPresentIllness?: string;
  pastMedicalHistory?: string;
  pastSurgicalHistory?: string;
  drugAndAllergyHistory?: string;
  familyAndPersonalHistory?: string;
  reviewOfSystems?: string;
  previousInvestigations?: string;
  currentMedications?: string;
  potentialRedFlags?: string;
  ayushAssessmentSummary?: string;
  physicianNotes?: string;
  status: 'AI_DRAFT' | 'PHYSICIAN_EDITED' | 'CONFIRMED' | 'REJECTED';
  safetyNotice: string;
  confirmedByDoctorName?: string;
  confirmedAt?: string;
  hisSynced: boolean;
  fhirResourceGenerated: boolean;
  fhirBundleJson?: string;
  generatedAt: string;
  updatedAt?: string;
}

export interface DoctorQueueItem {
  sessionId: number;
  patientId: number;
  tokenNumber: string;
  patientName: string;
  age: number;
  gender: string;
  abhaId?: string;
  chiefComplaint: string;
  intakeStatus: IntakeStatus;
  riskLevel: RiskLevel;
  redFlagTriggered: boolean;
  documentCount: number;
  ayushMode: boolean;
  completenessPercentage: number;
  startedAt: string;
  summaryId?: number;
}

export interface DoctorStats {
  totalQueueToday: number;
  readyForConsultation: number;
  redFlagsCount: number;
  avgIntakeCompletionTime: string;
  completedToday: number;
  doctorName: string;
  opdRoom: string;
}

export interface AuditEvent {
  id: number;
  patientId: number;
  eventType: string;
  description: string;
  performedBy: string;
  metadataJson?: string;
  timestamp: string;
}

export interface DoctorPatientDetail {
  patient: Patient;
  session: PatientSession;
  history?: ClinicalHistory;
  summary?: ClinicalSummary;
  documents: ClinicalDocument[];
  timeline: MedicalTimelineEvent[];
  redFlags: RedFlagEvent[];
  auditLogs: AuditEvent[];
}
