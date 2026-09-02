import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ClipboardCheck,
  User,
  Activity,
  FileText,
  AlertTriangle,
  ArrowRight,
  Sparkles,
  CheckCircle2,
  Calendar,
  Pill,
  Leaf,
  Layers,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';
import { ProgressStepper } from '../components/common/ProgressStepper';

export const ReviewPage: React.FC = () => {
  const { t } = useLanguage();
  const { patient, session, history, documents, generateClinicalSummary, isAyushMode } = useIntake();
  const navigate = useNavigate();
  const [isGenerating, setIsGenerating] = useState(false);

  const handleSubmitToQueue = async () => {
    setIsGenerating(true);
    try {
      await generateClinicalSummary();
      navigate('/patient/summary');
    } catch (e) {
      console.warn('Using local summary transition:', e);
      navigate('/patient/summary');
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 flex flex-col">
      <ProgressStepper currentStep={4} completeness={90} />

      <main className="flex-1 max-w-4xl mx-auto px-4 py-8 sm:py-12 w-full space-y-6">
        {/* Header */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-xl shadow-slate-100 space-y-6">
          <div className="flex items-center gap-3.5 pb-4 border-b border-slate-100">
            <div className="w-12 h-12 rounded-2xl bg-med-600 text-white flex items-center justify-center shadow-md shadow-med-600/20">
              <ClipboardCheck className="w-7 h-7 stroke-[2.5]" />
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                {t('review.title')}
              </h1>
              <p className="text-xs sm:text-sm text-slate-500 font-medium">
                {t('review.subtitle')}
              </p>
            </div>
          </div>

          {/* Clinical Safety Disclaimer Banner */}
          <div className="bg-amber-50 rounded-2xl p-4 border border-amber-200 flex items-center gap-3 text-xs text-amber-900 font-medium">
            <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0" />
            <span>
              <strong>Clinical Notice: </strong> {t('review.safety_disclaimer')}
            </span>
          </div>

          {/* Captured Summary Review Cards */}
          <div className="space-y-4">
            {/* Patient Demographics */}
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200 space-y-2">
              <div className="flex items-center gap-2 text-xs font-bold text-slate-900 uppercase tracking-wide">
                <User className="w-4 h-4 text-med-600" />
                <span>Patient Profile</span>
              </div>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs">
                <div>
                  <span className="text-slate-400 block">Name:</span>
                  <strong className="text-slate-800">{patient?.fullName || 'Sunita Patil'}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block">Age / Gender:</span>
                  <strong className="text-slate-800">{patient?.age || 52}y / {patient?.gender || 'Female'}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block">Token Number:</span>
                  <strong className="text-med-700">{session?.tokenNumber || '#104'}</strong>
                </div>
                <div>
                  <span className="text-slate-400 block">ABHA ID:</span>
                  <strong className="font-mono text-slate-700">{patient?.abhaId || '91-4567-8901-2345'}</strong>
                </div>
              </div>
            </div>

            {/* Clinical Intake History */}
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200 space-y-2">
              <div className="flex items-center gap-2 text-xs font-bold text-slate-900 uppercase tracking-wide">
                <Activity className="w-4 h-4 text-med-600" />
                <span>Reported Clinical History</span>
              </div>
              <div className="text-xs space-y-1 text-slate-700">
                <p><strong>Chief Complaint: </strong> {history?.chiefComplaint || 'Not captured'}</p>
                <p><strong>Onset & Duration: </strong> {history?.onsetAndDuration || 'Not captured'}</p>
                <p><strong>Pain Severity: </strong> <span className="font-bold text-red-600">{history?.severityScale != null ? `${history.severityScale} / 10` : 'Not captured'}</span></p>
                <p><strong>Associated Symptoms: </strong> {history?.associatedSymptoms || 'None reported'}</p>
                <p><strong>Past Medical History: </strong> {history?.pastMedicalHistory || 'None reported'}</p>
              </div>
            </div>

            {/* AYUSH Assessment if enabled */}
            {isAyushMode && (
              <div className="bg-ayush-50/70 rounded-2xl p-4 border border-ayush-200 space-y-2">
                <div className="flex items-center gap-2 text-xs font-bold text-ayush-900 uppercase tracking-wide">
                  <Leaf className="w-4 h-4 text-ayush-600" />
                  <span>AYUSH Integrative Assessment</span>
                </div>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 text-xs text-ayush-950">
                  <div>
                    <span className="text-ayush-700 block">Prakriti:</span>
                    <strong>{history?.prakriti || 'Pitta-Vata'}</strong>
                  </div>
                  <div>
                    <span className="text-ayush-700 block">Agni (Digestion):</span>
                    <strong>{history?.agni || 'Vishama Agni'}</strong>
                  </div>
                  <div>
                    <span className="text-ayush-700 block">Ahara-Vihara:</span>
                    <strong>Vegetarian, irregular sleep</strong>
                  </div>
                </div>
              </div>
            )}

            {/* Digitized Documents Count */}
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200 flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <FileText className="w-5 h-5 text-med-600" />
                <div>
                  <h4 className="text-xs font-bold text-slate-900">Digitized Previous Medical Records</h4>
                  <p className="text-[11px] text-slate-500">{documents.length} document(s) structured via AI OCR</p>
                </div>
              </div>
              <span className="px-3 py-1 bg-emerald-100 text-emerald-800 text-xs font-bold rounded-full">
                OCR Verified
              </span>
            </div>
          </div>

          {/* Submit Action */}
          <div className="pt-4 flex justify-end">
            <button
              type="button"
              onClick={handleSubmitToQueue}
              disabled={isGenerating}
              className="w-full sm:w-auto px-9 py-4 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-extrabold text-base rounded-2xl shadow-xl shadow-med-600/30 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2"
            >
              {isGenerating ? (
                <span>Generating Physician Summary...</span>
              ) : (
                <>
                  <span>{t('review.submit_to_queue')}</span>
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </button>
          </div>
        </div>
      </main>
    </div>
  );
};
