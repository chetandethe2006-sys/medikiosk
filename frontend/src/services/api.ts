import axios from 'axios';
import {
  Patient,
  PatientSession,
  ClinicalHistory,
  HistoryStepResponse,
  ClinicalDocument,
  ExtractedData,
  MedicalTimelineEvent,
  ClinicalSummary,
  RedFlagEvent,
  DoctorQueueItem,
  DoctorStats,
  DoctorPatientDetail,
  AuditEvent,
} from '../types';

const API_BASE_URL = (import.meta as any).env.VITE_API_BASE_URL || '/api';

const client = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_role');
      // Redirect handled by router guards
    }
    return Promise.reject(error);
  }
);

export const api = {
  // Authentication
  login: async (email: string, password: string) => {
    const res = await client.post('/auth/login', { email, password });
    if (res.data.token) {
      localStorage.setItem('jwt_token', res.data.token);
      localStorage.setItem('user_role', res.data.role);
    }
    return res.data;
  },
  googleLogin: async (credential: string, portalType: 'PATIENT' | 'DOCTOR') => {
    const res = await client.post('/auth/google', { credential, portalType });
    if (res.data.token) {
      localStorage.setItem('jwt_token', res.data.token);
      localStorage.setItem('user_role', res.data.role);
    }
    return res.data;
  },
  logout: () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
  },

  // Patient & Session
  createPatient: async (patient: Partial<Patient>): Promise<Patient> => {
    const res = await client.post('/patients', patient);
    return res.data;
  },
  getPatient: async (id: number): Promise<Patient> => {
    const res = await client.get(`/patients/${id}`);
    return res.data;
  },
  getPatientByAbha: async (abhaId: string): Promise<Patient> => {
    const res = await client.get(`/patients/abha/${encodeURIComponent(abhaId)}`);
    return res.data;
  },
  startSession: async (patientId: number, language: string, ayushMode: boolean): Promise<PatientSession> => {
    const res = await client.post('/sessions', { patientId, language, ayushMode });
    return res.data;
  },
  getSession: async (token: string): Promise<PatientSession> => {
    const res = await client.get(`/sessions/${token}`);
    return res.data;
  },
  updateSessionProgress: async (token: string, currentStep: string, completenessPercentage: number): Promise<PatientSession> => {
    const res = await client.put(`/sessions/${token}/progress`, { currentStep, completenessPercentage });
    return res.data;
  },
  recordConsent: async (data: {
    patientId: number;
    sessionToken?: string;
    dataCollectionConsented: boolean;
    aiAssistanceConsented: boolean;
    documentExtractionConsented: boolean;
    audioExplanationHeard: boolean;
  }) => {
    const res = await client.post('/consents', data);
    return res.data;
  },

  // Clinical History
  startHistory: async (data: {
    sessionToken: string;
    patientId: number;
    initialComplaint: string;
    language: string;
    ayushMode: boolean;
  }): Promise<HistoryStepResponse> => {
    const res = await client.post('/history/start', data);
    return res.data;
  },
  submitAnswer: async (sessionToken: string, data: {
    questionKey: string;
    questionText: string;
    patientAnswer: string;
    inputMode: 'TAP' | 'TEXT' | 'VOICE';
    stepOrder: number;
  }): Promise<HistoryStepResponse> => {
    const res = await client.post(`/history/${sessionToken}/answer`, data);
    return res.data;
  },
  getHistory: async (sessionToken: string): Promise<ClinicalHistory> => {
    const res = await client.get(`/history/${sessionToken}`);
    return res.data;
  },

  // Document Digitization & OCR
  uploadDocument: async (patientId: number, sessionToken: string, documentType: string, file?: File): Promise<ClinicalDocument> => {
    const formData = new FormData();
    formData.append('patientId', patientId.toString());
    formData.append('sessionToken', sessionToken);
    formData.append('documentType', documentType);
    if (file) {
      formData.append('file', file);
    }
    const res = await client.post('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
  addSampleDocument: async (sampleType: 'LAB' | 'PRESCRIPTION' | 'DISCHARGE', patientId: number, sessionToken: string): Promise<ClinicalDocument> => {
    const res = await client.post(`/documents/sample/${sampleType}`, { patientId, sessionToken });
    return res.data;
  },
  getDocumentsByPatient: async (patientId: number): Promise<ClinicalDocument[]> => {
    const res = await client.get(`/documents/patient/${patientId}`);
    return res.data;
  },
  updateExtractedParameter: async (id: number, parameterValue: string, physicianNote?: string): Promise<ExtractedData> => {
    const res = await client.put(`/documents/parameter/${id}`, { parameterValue, physicianNote });
    return res.data;
  },

  // Timeline
  getTimeline: async (patientId: number): Promise<MedicalTimelineEvent[]> => {
    const res = await client.get(`/timeline/patient/${patientId}`);
    return res.data;
  },

  // Clinical Summary
  generateSummary: async (sessionToken: string): Promise<ClinicalSummary> => {
    const res = await client.post('/summaries/generate', { sessionToken });
    return res.data;
  },
  getSummaryBySession: async (sessionToken: string): Promise<ClinicalSummary> => {
    const res = await client.get(`/summaries/session/${sessionToken}`);
    return res.data;
  },
  getSummaryByPatient: async (patientId: number): Promise<ClinicalSummary> => {
    const res = await client.get(`/summaries/patient/${patientId}`);
    return res.data;
  },
  editSummary: async (id: number, payload: any): Promise<ClinicalSummary> => {
    const res = await client.put(`/summaries/${id}`, payload);
    return res.data;
  },
  syncSummaryToHIS: async (id: number) => {
    const res = await client.post(`/summaries/${id}/sync-his`);
    return res.data;
  },

  // Red Flags & Triage
  getAllRedFlags: async (): Promise<RedFlagEvent[]> => {
    const res = await client.get('/red-flags');
    return res.data;
  },
  getPendingRedFlags: async (): Promise<RedFlagEvent[]> => {
    const res = await client.get('/red-flags/pending');
    return res.data;
  },
  acknowledgeRedFlag: async (id: number, doctorName: string): Promise<RedFlagEvent> => {
    const res = await client.put(`/red-flags/${id}/acknowledge`, { doctorName });
    return res.data;
  },

  // Doctor Dashboard
  getDoctorQueue: async (): Promise<DoctorQueueItem[]> => {
    const res = await client.get('/doctor/queue');
    return res.data;
  },
  getDoctorStats: async (): Promise<DoctorStats> => {
    const res = await client.get('/doctor/stats');
    return res.data;
  },
  getDoctorPatientDetail: async (patientId: number): Promise<DoctorPatientDetail> => {
    const res = await client.get(`/doctor/patient/${patientId}`);
    return res.data;
  },

  // Audit
  getPatientAuditLogs: async (patientId: number): Promise<AuditEvent[]> => {
    const res = await client.get(`/audit/patient/${patientId}`);
    return res.data;
  },
};
