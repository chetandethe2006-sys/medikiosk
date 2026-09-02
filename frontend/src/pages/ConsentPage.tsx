import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ShieldCheck,
  Volume2,
  CheckCircle,
  FileText,
  Lock,
  Cpu,
  UserCheck,
  AlertTriangle,
  ArrowRight,
  Sparkles,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';
import { ProgressStepper } from '../components/common/ProgressStepper';

export const ConsentPage: React.FC = () => {
  const { t, speakText, stopSpeaking, isSpeaking } = useLanguage();
  const { giveConsent } = useIntake();
  const navigate = useNavigate();
  const [agreed, setAgreed] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const consentPoints = [
    {
      icon: FileText,
      title: t('consent.point1_title'),
      desc: t('consent.point1_desc'),
      color: 'text-med-600 bg-med-50',
    },
    {
      icon: Cpu,
      title: t('consent.point2_title'),
      desc: t('consent.point2_desc'),
      color: 'text-teal-600 bg-teal-50',
    },
    {
      icon: Lock,
      title: t('consent.point3_title'),
      desc: t('consent.point3_desc'),
      color: 'text-indigo-600 bg-indigo-50',
    },
    {
      icon: AlertTriangle,
      title: t('consent.point4_title'),
      desc: t('consent.point4_desc'),
      color: 'text-amber-600 bg-amber-50',
    },
  ];

  const handleHearExplanation = () => {
    if (isSpeaking) {
      stopSpeaking();
    } else {
      const fullExplanation = `${t('consent.title')}. ${t('consent.point1_desc')} ${t('consent.point2_desc')}`;
      speakText(fullExplanation);
    }
  };

  const handleConsent = async () => {
    setIsSubmitting(true);
    try {
      await giveConsent();
      navigate('/patient/identity');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 flex flex-col">
      <ProgressStepper currentStep={1} completeness={20} />

      <main className="flex-1 max-w-4xl mx-auto px-4 py-8 sm:py-12 w-full space-y-6">
        {/* Main Card */}
        <div className="bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-xl shadow-slate-100 space-y-8">
          {/* Header */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-2xl bg-med-600 text-white flex items-center justify-center shadow-md shadow-med-600/20 flex-shrink-0">
                <ShieldCheck className="w-7 h-7 stroke-[2.5]" />
              </div>
              <div>
                <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                  {t('consent.title')}
                </h1>
                <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                  {t('consent.subtitle')}
                </p>
              </div>
            </div>

            {/* Hear Explanation Audio Button */}
            <button
              type="button"
              onClick={handleHearExplanation}
              className={`px-4 py-2.5 rounded-xl font-bold text-xs flex items-center gap-2 transition-all self-start sm:self-auto ${
                isSpeaking
                  ? 'bg-amber-100 text-amber-900 border border-amber-300 animate-pulse'
                  : 'bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200'
              }`}
            >
              <Volume2 className="w-4 h-4 text-med-600" />
              <span>{isSpeaking ? 'Playing Audio...' : t('consent.hear_explanation')}</span>
            </button>
          </div>

          {/* 4 Consent Explanations */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {consentPoints.map((p, i) => {
              const Icon = p.icon;
              return (
                <div
                  key={i}
                  className="bg-slate-50/80 rounded-2xl p-5 border border-slate-200/80 hover:border-med-300 transition-colors space-y-2"
                >
                  <div className="flex items-center gap-2.5">
                    <div className={`w-8 h-8 rounded-xl ${p.color} flex items-center justify-center flex-shrink-0`}>
                      <Icon className="w-4 h-4" />
                    </div>
                    <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wide">
                      {p.title}
                    </h3>
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed pl-10 font-medium">
                    {p.desc}
                  </p>
                </div>
              );
            })}
          </div>

          {/* Safety & Physician Disclaimer Alert */}
          <div className="bg-med-50/80 rounded-2xl p-4 border border-med-200 flex items-start gap-3 text-xs text-med-950">
            <UserCheck className="w-5 h-5 text-med-700 flex-shrink-0 mt-0.5" />
            <div className="leading-relaxed">
              <strong className="font-bold text-med-900">Clinical Decision Disclaimer: </strong>
              This platform compiles a drafted clinical summary for your consulting doctor. The physician conducts the final evaluation, clinical exam, and treatment plan.
            </div>
          </div>

          {/* Explicit Confirmation Checkbox */}
          <div className="pt-2">
            <label className="flex items-start gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-200 cursor-pointer hover:bg-slate-100/60 transition-colors">
              <input
                type="checkbox"
                checked={agreed}
                onChange={(e) => setAgreed(e.target.checked)}
                className="w-5 h-5 rounded text-med-600 focus:ring-med-500 border-slate-300 mt-0.5"
              />
              <span className="text-xs font-bold text-slate-800 leading-relaxed select-none">
                I understand the purpose of clinical data collection, AI history structuring, and authorize MediKiosk to prepare my clinical history for my doctor.
              </span>
            </label>
          </div>

          {/* Action CTAs */}
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-between gap-4">
            <p className="text-[11px] text-slate-400 text-center sm:text-left">
              {t('consent.policy_note')}
            </p>

            <button
              type="button"
              disabled={!agreed || isSubmitting}
              onClick={handleConsent}
              className={`w-full sm:w-auto px-8 py-4 rounded-2xl font-extrabold text-sm sm:text-base flex items-center justify-center gap-2 shadow-lg transition-all ${
                agreed && !isSubmitting
                  ? 'bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white shadow-med-600/30 hover:scale-[1.02] active:scale-[0.98]'
                  : 'bg-slate-200 text-slate-400 cursor-not-allowed shadow-none'
              }`}
            >
              <CheckCircle className="w-5 h-5" />
              <span>{t('consent.agree_button')}</span>
              <ArrowRight className="w-4 h-4 ml-1" />
            </button>
          </div>
        </div>
      </main>
    </div>
  );
};
