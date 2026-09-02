import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  CheckCircle2,
  Stethoscope,
  Clock,
  QrCode,
  FileCheck,
  ArrowRight,
  Sparkles,
  Building2,
  ShieldCheck,
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';
import { ProgressStepper } from '../components/common/ProgressStepper';

export const PatientSummaryPage: React.FC = () => {
  const { t } = useLanguage();
  const { patient, session, summary } = useIntake();
  const navigate = useNavigate();

  useEffect(() => {
    try {
      confetti({
        particleCount: 50,
        spread: 60,
        origin: { y: 0.6 },
      });
    } catch (e) {}
  }, []);

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 flex flex-col">
      <ProgressStepper currentStep={5} completeness={100} />

      <main className="flex-1 max-w-3xl mx-auto px-4 py-8 sm:py-12 w-full space-y-6">
        {/* Token Card */}
        <div className="bg-white rounded-3xl p-8 sm:p-10 border border-slate-200 shadow-xl text-center space-y-6">
          <div className="w-16 h-16 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center mx-auto shadow-md shadow-emerald-500/20">
            <CheckCircle2 className="w-10 h-10 stroke-[2.5]" />
          </div>

          <div className="space-y-1">
            <span className="text-xs font-extrabold uppercase tracking-widest text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full border border-emerald-200">
              Intake Completed & Queued
            </span>
            <h1 className="text-2xl sm:text-3xl font-black text-slate-900 mt-2">
              Your Clinical Draft is Ready for the Doctor
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 font-medium">
              Please proceed to OPD Waiting Hall. Your token has been dispatched to Dr. Rajesh Sharma.
            </p>
          </div>

          {/* Large Token Badge */}
          <div className="bg-gradient-to-br from-med-50 via-teal-50 to-emerald-50 rounded-3xl p-6 border-2 border-med-200 max-w-sm mx-auto shadow-inner space-y-2">
            <span className="text-[11px] font-extrabold uppercase tracking-wider text-slate-500">
              Your OPD Queue Token
            </span>
            <div className="text-4xl sm:text-5xl font-black text-med-800 tracking-tight">
              {session?.tokenNumber || '#104'}
            </div>
            <div className="flex items-center justify-center gap-4 text-xs font-bold text-slate-700 pt-2 border-t border-med-200/60">
              <span className="flex items-center gap-1">
                <Building2 className="w-3.5 h-3.5 text-med-600" />
                OPD Room 104
              </span>
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5 text-med-600" />
                Est. Wait: 6 mins
              </span>
            </div>
          </div>

          {/* Summary Details */}
          <div className="bg-slate-50 rounded-2xl p-5 border border-slate-200 text-left space-y-2 text-xs text-slate-700">
            <div className="flex justify-between border-b border-slate-200/70 pb-2">
              <span className="text-slate-400">Patient:</span>
              <strong className="text-slate-900">{patient?.fullName || 'Sunita Patil'} ({patient?.age || 52}y / {patient?.gender || 'Female'})</strong>
            </div>
            <div className="flex justify-between border-b border-slate-200/70 pb-2">
              <span className="text-slate-400">ABHA Health ID:</span>
              <strong className="font-mono text-slate-900">{patient?.abhaId || '91-4567-8901-2345'}</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Clinical Draft Status:</span>
              <span className="font-bold text-med-700">Awaiting Physician Review & Verification</span>
            </div>
          </div>

          {/* Switch to Doctor View CTA */}
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-3">
            <Link
              to="/doctor"
              className="w-full sm:w-auto px-8 py-4 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-extrabold text-sm sm:text-base rounded-2xl shadow-lg shadow-med-600/30 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2"
            >
              <Stethoscope className="w-5 h-5" />
              <span>Open Doctor Dashboard (Next Step in Demo)</span>
              <ArrowRight className="w-4 h-4 ml-1" />
            </Link>

            <Link
              to="/"
              className="w-full sm:w-auto px-6 py-4 bg-white hover:bg-slate-50 text-slate-700 font-bold text-sm rounded-2xl border border-slate-200 transition-colors"
            >
              Back to Home
            </Link>
          </div>
        </div>
      </main>
    </div>
  );
};
