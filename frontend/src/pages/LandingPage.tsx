import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Activity,
  ArrowRight,
  Stethoscope,
  Sparkles,
  ShieldAlert,
  FileCheck2,
  Languages,
  Clock,
  CheckCircle2,
  Building2,
  ChevronRight,
  PlayCircle,
  QrCode,
  Layers,
  HeartPulse,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';

export const LandingPage: React.FC = () => {
  const { t } = useLanguage();
  const { loadDemoPatient } = useIntake();
  const navigate = useNavigate();

  const handleLaunchDemo = async () => {
    await loadDemoPatient('SUNITA');
    navigate('/doctor/patient/1');
  };

  const handleStartIntakeDemo = async () => {
    navigate('/patient/login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 via-white to-slate-100 text-slate-900 flex flex-col">
      {/* Top Prototype Badge */}
      <div className="bg-gradient-to-r from-med-700 via-teal-700 to-med-800 text-white text-xs py-2 px-4 text-center font-semibold tracking-wide flex items-center justify-center gap-2">
        <span className="inline-block w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
        <span>Smart India Hackathon 2026 Prototype • Ministry of Ayush / All India Institute of Ayurveda</span>
      </div>

      {/* Main Hero Container */}
      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 sm:py-16">
        <div className="text-center max-w-3xl mx-auto space-y-6">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-med-50 border border-med-200 text-med-800 text-xs font-bold shadow-xs">
            <Sparkles className="w-3.5 h-3.5 text-med-600" />
            <span>Self-Service Clinical Intake Kiosk</span>
          </div>

          {/* Main Title */}
          <h1 className="text-3xl sm:text-5xl lg:text-6xl font-black text-slate-950 tracking-tight leading-[1.15]">
            From Patient Story to <br className="hidden sm:inline" />
            <span className="bg-gradient-to-r from-med-600 via-teal-600 to-emerald-600 bg-clip-text text-transparent">
              Physician-Ready History
            </span>
          </h1>

          {/* Subtitle */}
          <p className="text-base sm:text-lg text-slate-600 font-medium max-w-2xl mx-auto leading-relaxed">
            Capture patient clinical history in Indian languages, digitize previous medical records with OCR, detect potential red flags in real-time, and generate a structured 30-second physician draft before consultation.
          </p>

          {/* Action CTAs */}
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-3.5">
            <button
              onClick={handleStartIntakeDemo}
              className="w-full sm:w-auto px-8 py-4 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white text-base font-extrabold rounded-2xl shadow-lg shadow-med-600/30 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2.5"
            >
              <span>Start Patient Intake</span>
              <ArrowRight className="w-5 h-5" />
            </button>

            <Link
              to="/doctor"
              className="w-full sm:w-auto px-7 py-4 bg-white hover:bg-slate-50 text-slate-800 text-base font-bold rounded-2xl border-2 border-slate-200 shadow-sm hover:border-slate-300 transition-all flex items-center justify-center gap-2"
            >
              <Stethoscope className="w-5 h-5 text-med-600" />
              <span>Doctor Dashboard</span>
            </Link>

            <button
              onClick={handleLaunchDemo}
              className="w-full sm:w-auto px-6 py-4 bg-indigo-50 hover:bg-indigo-100 text-indigo-800 text-base font-bold rounded-2xl border border-indigo-200 transition-all flex items-center justify-center gap-2"
            >
              <PlayCircle className="w-5 h-5 text-indigo-600" />
              <span>Launch Demo Case</span>
            </button>
          </div>
        </div>

        {/* Traditional vs MediKiosk Transformation Card */}
        <div className="mt-14 max-w-4xl mx-auto bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-xl shadow-slate-100">
          <div className="text-center mb-6">
            <h2 className="text-xs font-extrabold uppercase tracking-widest text-slate-400">The Problem & Transformation</h2>
            <p className="text-xl font-bold text-slate-900 mt-1">Reinventing OPD Case-Taking for Indian Hospitals</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Traditional */}
            <div className="bg-red-50/60 rounded-2xl p-5 border border-red-200/80 space-y-3">
              <div className="flex items-center gap-2 text-red-800 font-extrabold text-sm">
                <Clock className="w-4 h-4 text-red-600" />
                <span>Traditional OPD Workflow (Bottleneck)</span>
              </div>
              <p className="text-xs text-red-950 font-medium leading-relaxed">
                Patient waits in long queues → Doctor spends 80% consultation time asking repetitive history questions → Patient struggles to explain timeline → Searching unorganized paper records → High physician burnout.
              </p>
            </div>

            {/* MediKiosk */}
            <div className="bg-emerald-50/70 rounded-2xl p-5 border border-emerald-300 space-y-3 shadow-xs">
              <div className="flex items-center gap-2 text-emerald-900 font-extrabold text-sm">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                <span>MediKiosk Solution (Streamlined)</span>
              </div>
              <p className="text-xs text-emerald-950 font-medium leading-relaxed">
                Self-service intake at kiosk → AI conversational interview in local language → Scan & extract previous records → Real-time red flag triage → Doctor reviews structured 30s summary instantly.
              </p>
            </div>
          </div>
        </div>

        {/* 5-Step Visual Workflow */}
        <div className="mt-16 text-center">
          <h3 className="text-xs font-extrabold uppercase tracking-widest text-med-700 mb-2">
            5-Stage Intake Journey
          </h3>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-950">
            How MediKiosk Works End-to-End
          </h2>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mt-8 max-w-6xl mx-auto">
            {[
              {
                step: '1',
                title: 'Identify',
                desc: 'Scan ABHA ID or enter basic demographics at the hospital entrance.',
                icon: QrCode,
                color: 'bg-blue-500',
              },
              {
                step: '2',
                title: 'Converse',
                desc: 'Speak or tap responses in Marathi, Hindi, or English with adaptive AI.',
                icon: Languages,
                color: 'bg-teal-500',
              },
              {
                step: '3',
                title: 'Scan Records',
                desc: 'Digitize previous lab reports, prescriptions, and discharge summaries.',
                icon: FileCheck2,
                color: 'bg-emerald-500',
              },
              {
                step: '4',
                title: 'Summarize',
                desc: 'AI compiles physician draft with safety notices and 30s quick view.',
                icon: Layers,
                color: 'bg-indigo-500',
              },
              {
                step: '5',
                title: 'Consult',
                desc: 'Physician reviews, verifies, and syncs data to hospital HIS & ABDM.',
                icon: Stethoscope,
                color: 'bg-purple-500',
              },
            ].map((s) => {
              const Icon = s.icon;
              return (
                <div
                  key={s.step}
                  className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm text-left hover:shadow-md transition-all flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-center justify-between mb-3">
                      <span className="text-xs font-black text-slate-400">STAGE {s.step}</span>
                      <div className={`w-8 h-8 rounded-xl ${s.color} text-white flex items-center justify-center shadow-xs`}>
                        <Icon className="w-4 h-4" />
                      </div>
                    </div>
                    <h4 className="text-base font-extrabold text-slate-900">{s.title}</h4>
                    <p className="text-xs text-slate-600 mt-1.5 leading-relaxed">{s.desc}</p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Why MediKiosk Feature Pillars */}
        <div className="mt-20 max-w-6xl mx-auto">
          <div className="text-center mb-10">
            <h3 className="text-xs font-extrabold uppercase tracking-widest text-slate-400">Key Innovations</h3>
            <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-950 mt-1">Built Specifically for Indian Healthcare</h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:border-med-500/50 transition-colors">
              <div className="w-12 h-12 rounded-xl bg-red-100 text-red-700 flex items-center justify-center mb-4">
                <ShieldAlert className="w-6 h-6" />
              </div>
              <h3 className="text-base font-extrabold text-slate-900">Rule-Based Red Flag Detection</h3>
              <p className="text-xs text-slate-600 mt-2 leading-relaxed">
                Immediately catches high-risk symptom clusters (e.g. Chest pain + diaphoresis) and dispatches priority notifications to the triage desk before the patient even enters the OPD.
              </p>
            </div>

            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:border-med-500/50 transition-colors">
              <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-800 flex items-center justify-center mb-4">
                <HeartPulse className="w-6 h-6" />
              </div>
              <h3 className="text-base font-extrabold text-slate-900">AIIA AYUSH Integrative Mode</h3>
              <p className="text-xs text-slate-600 mt-2 leading-relaxed">
                Built-in support for Ministry of Ayush clinical parameters including Prakriti assessment, Agni evaluation, Ahara-Vihara dietetics, and Ayurvedic disease pathogenesis (Nidana).
              </p>
            </div>

            <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm hover:border-med-500/50 transition-colors">
              <div className="w-12 h-12 rounded-xl bg-med-100 text-med-800 flex items-center justify-center mb-4">
                <Clock className="w-6 h-6" />
              </div>
              <h3 className="text-base font-extrabold text-slate-900">"Doctor in 30 Seconds" Quick View</h3>
              <p className="text-xs text-slate-600 mt-2 leading-relaxed">
                A purpose-built high-density summary card giving physicians the chief complaint, key symptoms, potential red flags, past history, and active medications in 30 seconds.
              </p>
            </div>
          </div>
        </div>

        {/* Prototype Notice Section */}
        <div className="mt-16 bg-slate-100 rounded-2xl p-6 border border-slate-200 text-xs text-slate-600 max-w-4xl mx-auto text-center space-y-1">
          <p className="font-bold text-slate-800">Smart India Hackathon 2026 — Prototype Notice</p>
          <p>
            This clinical software prototype does not provide medical diagnoses. All AI outputs are framed as drafts requiring physician review. Demonstrates approximately the first 30% vertical slice with pluggable AI adapters and deterministic fallback data.
          </p>
        </div>
      </main>

      {/* Footer */}
      <footer className="w-full bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <p>© 2026 MediKiosk • All India Institute of Ayurveda & Ministry of Ayush</p>
          <div className="flex items-center gap-4">
            <Link to="/doctor" className="hover:text-med-600 font-semibold">Doctor Portal</Link>
            <Link to="/triage" className="hover:text-red-600 font-semibold">Triage Desk</Link>
            <Link to="/settings" className="hover:text-slate-800">System Architecture</Link>
          </div>
        </div>
      </footer>
    </div>
  );
};
