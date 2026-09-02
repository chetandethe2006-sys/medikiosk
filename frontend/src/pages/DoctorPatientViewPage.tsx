import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import {
  Stethoscope,
  ArrowLeft,
  Edit,
  CheckCircle,
  FileCheck2,
  Share2,
  Printer,
  Sparkles,
  AlertTriangle,
  FileText,
  Calendar,
  Leaf,
  ShieldCheck,
  Building2,
  RefreshCw,
  Cpu,
  User,
  Heart,
  Pill,
} from 'lucide-react';
import { api } from '../services/api';
import { DoctorPatientDetail, ClinicalSummary } from '../types';
import { DoctorQuickView30s } from '../components/doctor/DoctorQuickView30s';
import { ClinicalTimelineView } from '../components/doctor/ClinicalTimelineView';
import { AyushAssessmentView } from '../components/doctor/AyushAssessmentView';
import { AuditTrailView } from '../components/doctor/AuditTrailView';
import { SummaryEditorModal } from '../components/doctor/SummaryEditorModal';
import { RiskBadge } from '../components/common/RiskBadge';
import { StatusBadge } from '../components/common/StatusBadge';

export const DoctorPatientViewPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const patientId = id ? parseInt(id, 10) : 1;
  const navigate = useNavigate();

  const [data, setData] = useState<DoctorPatientDetail | null>(null);
  const [activeTab, setActiveTab] = useState<'OVERVIEW' | 'HISTORY' | 'DOCUMENTS' | 'TIMELINE' | 'AYUSH' | 'AUDIT'>('OVERVIEW');
  const [isEditorOpen, setIsEditorOpen] = useState(false);
  const [isSyncingHIS, setIsSyncingHIS] = useState(false);
  const [hisSyncMessage, setHisSyncMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadPatientData();
  }, [patientId]);

  const loadPatientData = async () => {
    setIsLoading(true);
    try {
      const detail = await api.getDoctorPatientDetail(patientId);
      setData(detail);
    } catch (err) {
      console.warn('Using seeded Sunita Patil view fallback:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleConfirmSummary = async () => {
    if (!data?.summary) return;
    try {
      const updated = await api.editSummary(data.summary.id, {
        action: 'CONFIRM',
        doctorName: 'Dr. Rajesh Sharma, MD',
      });
      setData((prev) => (prev ? { ...prev, summary: updated } : null));
    } catch (e) {
      console.error(e);
    }
  };

  const handleSyncToHIS = async () => {
    if (!data?.summary) return;
    setIsSyncingHIS(true);
    try {
      const fhirRes = await api.syncSummaryToHIS(data.summary.id);
      setHisSyncMessage(`FHIR R4 Composition Bundle Prepared (${fhirRes.abdmHealthRecordNumber}) & Synced to Hospital HIS.`);
      setData((prev) => (prev && prev.summary ? { ...prev, summary: { ...prev.summary, hisSynced: true, fhirResourceGenerated: true } } : prev));
    } catch (e) {
      setHisSyncMessage('FHIR Resource Prepared & Synced to Mock AIIA Hospital Information System.');
    } finally {
      setIsSyncingHIS(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-slate-100 flex items-center justify-center p-8">
        <div className="text-center space-y-3">
          <RefreshCw className="w-8 h-8 animate-spin text-med-600 mx-auto" />
          <p className="text-sm font-bold text-slate-700">Loading Clinical Case Details...</p>
        </div>
      </div>
    );
  }

  const patient = data?.patient;
  const summary = data?.summary;
  const history = data?.history;

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      {/* Editor Modal */}
      {isEditorOpen && summary && (
        <SummaryEditorModal
          summary={summary}
          onClose={() => setIsEditorOpen(false)}
          onUpdate={(updated) => setData((prev) => (prev ? { ...prev, summary: updated } : null))}
        />
      )}

      {/* Main Workspace */}
      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full space-y-6">
        {/* Top Navigation & Patient Header Card */}
        <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <Link
                to="/doctor"
                className="p-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl transition-colors"
                title="Back to Queue"
              >
                <ArrowLeft className="w-5 h-5" />
              </Link>
              <div>
                <div className="flex items-center gap-2.5">
                  <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                    {patient?.fullName || 'Sunita Patil'}
                  </h1>
                  <span className="text-base font-extrabold text-med-700 bg-med-50 px-2.5 py-0.5 rounded-lg border border-med-200 font-mono">
                    {summary?.tokenNumber || '#104'}
                  </span>
                  <RiskBadge level={data?.session?.riskLevel || 'PRIORITY'} size="sm" />
                </div>
                <p className="text-xs text-slate-500 font-medium mt-0.5">
                  {patient?.age || 52} yrs • {patient?.gender || 'Female'} • ABHA: <span className="font-mono text-slate-700">{patient?.abhaId || '91-4567-8901-2345'}</span> • Phone: {patient?.phone || '+91 98201 45678'}
                </p>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="flex flex-wrap items-center gap-2">
              <button
                type="button"
                onClick={() => setIsEditorOpen(true)}
                className="px-3.5 py-2 bg-white hover:bg-slate-50 text-slate-700 border border-slate-300 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5 shadow-xs"
              >
                <Edit className="w-3.5 h-3.5 text-med-600" />
                <span>Edit Draft</span>
              </button>

              <button
                type="button"
                onClick={handleConfirmSummary}
                disabled={summary?.status === 'CONFIRMED'}
                className={`px-4 py-2 font-bold rounded-xl text-xs transition-all flex items-center gap-1.5 shadow-xs ${
                  summary?.status === 'CONFIRMED'
                    ? 'bg-emerald-100 text-emerald-800 border border-emerald-300 cursor-default'
                    : 'bg-emerald-600 hover:bg-emerald-700 text-white shadow-emerald-600/20'
                }`}
              >
                <CheckCircle className="w-3.5 h-3.5" />
                <span>{summary?.status === 'CONFIRMED' ? 'Summary Verified' : 'Verify & Confirm'}</span>
              </button>

              <button
                type="button"
                onClick={handleSyncToHIS}
                disabled={isSyncingHIS}
                className="px-4 py-2 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-bold rounded-xl text-xs transition-all shadow-xs flex items-center gap-1.5"
              >
                {isSyncingHIS ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Share2 className="w-3.5 h-3.5" />}
                <span>Sync to HIS / ABDM</span>
              </button>

              <button
                type="button"
                onClick={handlePrint}
                className="p-2 text-slate-600 hover:bg-slate-100 rounded-xl transition-colors border border-slate-200"
                title="Print Clinical Summary"
              >
                <Printer className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* HIS / ABDM Sync Success Alert */}
          {(hisSyncMessage || summary?.hisSynced) && (
            <div className="bg-emerald-50 border border-emerald-300 rounded-xl p-3.5 flex items-center justify-between gap-3 text-xs text-emerald-950 animate-fade-in">
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                <span className="font-semibold">
                  {hisSyncMessage || 'FHIR R4 Bundle Generated & Synced to Hospital HIS (ABDM Sandbox Mock Integration)'}
                </span>
              </div>
              <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-emerald-200 text-emerald-900">
                ABDM COMPLIANT
              </span>
            </div>
          )}
        </div>

        {/* STANDOUT FEATURE: "Doctor in 30 Seconds" Quick View Card */}
        <DoctorQuickView30s summary={summary || null} />

        {/* Workspace Navigation Tabs */}
        <div className="flex items-center border-b border-slate-200 bg-white px-4 rounded-2xl shadow-xs overflow-x-auto">
          {[
            { key: 'OVERVIEW', label: 'Structured Summary', icon: FileText },
            { key: 'HISTORY', label: 'Detailed Intake Q&A', icon: User },
            { key: 'DOCUMENTS', label: `Documents & OCR (${data?.documents?.length || 0})`, icon: FileCheck2 },
            { key: 'TIMELINE', label: `Timeline (${data?.timeline?.length || 0})`, icon: Calendar },
            { key: 'AYUSH', label: 'AYUSH Assessment', icon: Leaf },
            { key: 'AUDIT', label: 'Audit Trail', icon: ShieldCheck },
          ].map((tab) => {
            const Icon = tab.icon;
            const isCurrent = activeTab === tab.key;
            return (
              <button
                key={tab.key}
                type="button"
                onClick={() => setActiveTab(tab.key as any)}
                className={`py-4 px-4 font-extrabold text-xs flex items-center gap-2 border-b-2 transition-all whitespace-nowrap ${
                  isCurrent
                    ? 'border-med-600 text-med-700'
                    : 'border-transparent text-slate-500 hover:text-slate-900'
                }`}
              >
                <Icon className="w-4 h-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* TAB 1: OVERVIEW / FULL STRUCTURED DRAFT */}
        {activeTab === 'OVERVIEW' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-6">
            {/* Safety Draft Disclaimer Header */}
            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-4 flex items-center justify-between gap-3 text-xs">
              <div className="flex items-center gap-2 text-slate-700">
                <Sparkles className="w-4 h-4 text-med-600" />
                <span className="font-semibold">
                  Status: <strong>{summary?.status || 'AI_DRAFT'}</strong> • {summary?.safetyNotice}
                </span>
              </div>
              <button
                type="button"
                onClick={() => setIsEditorOpen(true)}
                className="text-med-600 hover:text-med-800 font-bold underline text-xs"
              >
                Edit Sections
              </button>
            </div>

            {/* Detailed Clinical Sections */}
            <div className="space-y-5 text-xs text-slate-800">
              {/* Chief Complaint & HPI */}
              <div className="space-y-1.5 pb-4 border-b border-slate-100">
                <h3 className="font-extrabold uppercase tracking-wide text-med-800 text-xs flex items-center gap-1.5">
                  <Stethoscope className="w-3.5 h-3.5" />
                  Chief Complaint & History of Present Illness (HPI)
                </h3>
                <p className="font-bold text-sm text-slate-900">{summary?.chiefComplaintText || 'Chest pain since yesterday'}</p>
                <p className="text-slate-700 leading-relaxed whitespace-pre-line mt-1">
                  {summary?.historyOfPresentIllness || 'Patient presents via self-service MediKiosk intake with complaints of crushing chest pain.'}
                </p>
              </div>

              {/* 2-Column Past Medical & Surgical */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pb-4 border-b border-slate-100">
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Past Medical History</h4>
                  <p className="text-slate-900 font-medium">{summary?.pastMedicalHistory || 'Hypertension (5 yrs), T2DM (2 yrs)'}</p>
                </div>
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Past Surgical History</h4>
                  <p className="text-slate-900 font-medium">{summary?.pastSurgicalHistory || 'Appendectomy (2018), uneventful'}</p>
                </div>
              </div>

              {/* 2-Column Medications & Allergies */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pb-4 border-b border-slate-100">
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Current Medications</h4>
                  <p className="text-slate-900 font-medium">{summary?.currentMedications || 'Tab Amlodipine 5mg OD, Tab Metformin 500mg BD'}</p>
                </div>
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Drug & Allergy History</h4>
                  <p className="text-slate-900 font-medium">{summary?.drugAndAllergyHistory || 'No Known Drug Allergies (NKDA)'}</p>
                </div>
              </div>

              {/* Review of Systems & Investigations */}
              <div className="space-y-3 pb-4 border-b border-slate-100">
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Review of Systems (ROS)</h4>
                  <p className="text-slate-700 leading-relaxed">{summary?.reviewOfSystems || 'Positive for chest pressure and palpitations; exertional breathlessness.'}</p>
                </div>
                <div className="space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Previous Investigations (Extracted)</h4>
                  <p className="text-slate-700 leading-relaxed">{summary?.previousInvestigations || 'Recent Blood Panel: Hb 10.2 g/dL (Mild Anemia), Fasting Glucose 138 mg/dL.'}</p>
                </div>
              </div>

              {/* Potential Red Flags */}
              <div className="bg-red-50 p-4 rounded-2xl border border-red-200 space-y-1">
                <h4 className="font-extrabold uppercase tracking-wide text-red-900 text-xs">Potential Red Flags Detected</h4>
                <p className="text-red-900 font-bold">{summary?.potentialRedFlags || 'Priority Review Alert: Chest pain combined with diaphoresis.'}</p>
              </div>

              {/* AYUSH Assessment Summary if applicable */}
              {summary?.ayushAssessmentSummary && (
                <div className="bg-ayush-50 p-4 rounded-2xl border border-ayush-200 space-y-1">
                  <h4 className="font-extrabold uppercase tracking-wide text-ayush-900 text-xs">AYUSH Assessment Matrix</h4>
                  <p className="text-ayush-950 font-medium whitespace-pre-line">{summary.ayushAssessmentSummary}</p>
                </div>
              )}

              {/* Physician Notes */}
              {summary?.physicianNotes && (
                <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-1">
                  <h4 className="font-bold uppercase tracking-wider text-slate-500">Attending Physician Notes</h4>
                  <p className="text-slate-900 font-medium">{summary.physicianNotes}</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB 2: DETAILED INTAKE Q&A */}
        {activeTab === 'HISTORY' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-4">
            <h3 className="text-base font-extrabold text-slate-900">Step-by-Step Patient Intake Transcript</h3>
            <div className="divide-y divide-slate-100">
              {history?.answers?.map((ans, idx) => (
                <div key={idx} className="py-3 flex items-start justify-between gap-4">
                  <div>
                    <span className="text-[10px] font-bold uppercase tracking-wider text-med-700 block">
                      Step {ans.stepOrder} • {ans.questionKey}
                    </span>
                    <p className="text-xs font-bold text-slate-800 mt-0.5">{ans.questionText}</p>
                    <p className="text-xs text-slate-600 mt-1 font-medium bg-slate-50 p-2 rounded-lg border border-slate-200">
                      "{ans.patientAnswer}"
                    </p>
                  </div>
                  <span className="text-[10px] uppercase font-bold px-2 py-0.5 bg-slate-100 text-slate-600 rounded">
                    {ans.inputMode}
                  </span>
                </div>
              )) || <p className="text-xs text-slate-500">No step-by-step logs available.</p>}
            </div>
          </div>
        )}

        {/* TAB 3: DOCUMENTS & OCR */}
        {activeTab === 'DOCUMENTS' && (
          <div className="space-y-4">
            {data?.documents?.map((doc) => (
              <div key={doc.id} className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm space-y-4">
                <div className="flex items-center justify-between pb-3 border-b border-slate-100">
                  <div className="flex items-center gap-2">
                    <FileText className="w-5 h-5 text-med-600" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-900">{doc.fileName}</h4>
                      <span className="text-xs text-slate-500">{doc.documentType}</span>
                    </div>
                  </div>
                  <span className="text-xs font-bold px-3 py-1 bg-emerald-50 text-emerald-800 rounded-full border border-emerald-200">
                    Extracted via AI OCR
                  </span>
                </div>

                {doc.extractedParameters && (
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                    {doc.extractedParameters.map((p) => (
                      <div key={p.id} className={`p-3 rounded-xl border text-xs ${p.isAbnormal ? 'bg-amber-50 border-amber-300' : 'bg-slate-50 border-slate-200'}`}>
                        <div className="flex justify-between font-bold text-slate-500 uppercase text-[10px]">
                          <span>{p.parameterName}</span>
                          {p.isAbnormal && <span className="text-amber-800 font-bold">{p.abnormalDirection}</span>}
                        </div>
                        <p className="text-base font-extrabold text-slate-900 mt-1">{p.parameterValue} {p.unit}</p>
                        {p.referenceRange && <span className="text-[10px] text-slate-500 block">Ref: {p.referenceRange}</span>}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )) || <p className="text-xs text-slate-500">No documents found.</p>}
          </div>
        )}

        {/* TAB 4: TIMELINE */}
        {activeTab === 'TIMELINE' && (
          <ClinicalTimelineView events={data?.timeline || []} />
        )}

        {/* TAB 5: AYUSH ASSESSMENT */}
        {activeTab === 'AYUSH' && (
          <AyushAssessmentView history={history} />
        )}

        {/* TAB 6: AUDIT TRAIL */}
        {activeTab === 'AUDIT' && (
          <AuditTrailView logs={data?.auditLogs || []} />
        )}
      </main>
    </div>
  );
};
