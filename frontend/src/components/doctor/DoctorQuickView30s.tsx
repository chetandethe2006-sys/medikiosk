import React from 'react';
import {
  Clock,
  AlertOctagon,
  Heart,
  Pill,
  FileCheck,
  Stethoscope,
  Sparkles,
} from 'lucide-react';
import { ClinicalSummary } from '../../types';

interface DoctorQuickView30sProps {
  summary: ClinicalSummary | null;
}

export const DoctorQuickView30s: React.FC<DoctorQuickView30sProps> = ({ summary }) => {
  if (!summary) return null;

  const isRedFlag = summary.quickViewRedFlagSummary?.includes('YES') || summary.potentialRedFlags?.includes('PRIORITY');

  return (
    <div className="w-full bg-gradient-to-br from-slate-900 via-med-950 to-slate-900 text-white rounded-2xl p-5 sm:p-6 shadow-xl border border-med-800/40 relative overflow-hidden">
      {/* Background ambient medical accent */}
      <div className="absolute top-0 right-0 -mt-10 -mr-10 w-48 h-48 bg-teal-500/10 rounded-full blur-3xl pointer-events-none" />

      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-2 pb-4 mb-4 border-b border-slate-800">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-med-500/20 border border-med-400/30 flex items-center justify-center text-med-400">
            <Clock className="w-4 h-4" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-sm sm:text-base font-extrabold tracking-wide text-white uppercase">
                Doctor in 30 Seconds
              </h2>
              <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-med-500/20 text-med-300 border border-med-500/30">
                <Sparkles className="w-2.5 h-2.5" /> High-Density View
              </span>
            </div>
            <p className="text-[11px] text-slate-400">
              Scannable clinical case synthesis generated prior to consultation
            </p>
          </div>
        </div>

        <div className="text-right">
          <span className="text-[11px] text-slate-400 block">Intake Token</span>
          <span className="text-sm font-extrabold text-teal-400">{summary.tokenNumber || '#104'}</span>
        </div>
      </div>

      {/* 6-Grid Clinical Scannable Tiles */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3.5">
        {/* 1. Chief Complaint */}
        <div className="bg-slate-800/70 border border-slate-700/60 rounded-xl p-3.5 hover:border-med-500/40 transition-colors">
          <div className="flex items-center gap-2 text-med-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Stethoscope className="w-3.5 h-3.5" />
            <span>Chief Complaint</span>
          </div>
          <p className="text-sm font-bold text-white leading-snug">
            {summary.quickViewChiefComplaint || summary.chiefComplaintText || 'Chest pain for 2 days'}
          </p>
        </div>

        {/* 2. Key Symptoms */}
        <div className="bg-slate-800/70 border border-slate-700/60 rounded-xl p-3.5 hover:border-med-500/40 transition-colors">
          <div className="flex items-center gap-2 text-teal-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Heart className="w-3.5 h-3.5" />
            <span>Associated Symptoms</span>
          </div>
          <p className="text-sm font-medium text-slate-200 leading-snug">
            {summary.quickViewKeySymptoms || 'Breathlessness, diaphoresis, radiating to left arm'}
          </p>
        </div>

        {/* 3. Potential Red Flags */}
        <div
          className={`rounded-xl p-3.5 border transition-colors ${
            isRedFlag
              ? 'bg-red-950/70 border-red-500/60 text-red-100 shadow-sm shadow-red-900/20'
              : 'bg-emerald-950/50 border-emerald-500/40 text-emerald-100'
          }`}
        >
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider mb-1">
            <AlertOctagon className={`w-3.5 h-3.5 ${isRedFlag ? 'text-red-400' : 'text-emerald-400'}`} />
            <span className={isRedFlag ? 'text-red-300' : 'text-emerald-300'}>Potential Red Flag</span>
          </div>
          <p className="text-sm font-bold leading-snug">
            {summary.quickViewRedFlagSummary || (isRedFlag ? 'YES – Priority Review' : 'NO – Standard Triage')}
          </p>
        </div>

        {/* 4. Past Medical History */}
        <div className="bg-slate-800/70 border border-slate-700/60 rounded-xl p-3.5 hover:border-med-500/40 transition-colors">
          <div className="flex items-center gap-2 text-amber-400 text-xs font-bold uppercase tracking-wider mb-1">
            <FileCheck className="w-3.5 h-3.5" />
            <span>Past History</span>
          </div>
          <p className="text-sm font-medium text-slate-200 leading-snug">
            {summary.quickViewPastHistory || summary.pastMedicalHistory || 'Hypertension (5y), T2DM (2y)'}
          </p>
        </div>

        {/* 5. Current Medications */}
        <div className="bg-slate-800/70 border border-slate-700/60 rounded-xl p-3.5 hover:border-med-500/40 transition-colors">
          <div className="flex items-center gap-2 text-blue-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Pill className="w-3.5 h-3.5" />
            <span>Active Medications</span>
          </div>
          <p className="text-sm font-medium text-slate-200 leading-snug">
            {summary.quickViewCurrentMeds || summary.currentMedications || 'Tab Amlodipine 5mg OD, Tab Metformin 500mg BD'}
          </p>
        </div>

        {/* 6. Recent Labs & OCR */}
        <div className="bg-slate-800/70 border border-slate-700/60 rounded-xl p-3.5 hover:border-med-500/40 transition-colors">
          <div className="flex items-center gap-2 text-indigo-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Recent Key Labs</span>
          </div>
          <p className="text-sm font-medium text-slate-200 leading-snug">
            {summary.quickViewRecentLabs || 'Hb 10.2 g/dL (Low), Fasting Glucose 138 mg/dL (High)'}
          </p>
        </div>
      </div>
    </div>
  );
};
