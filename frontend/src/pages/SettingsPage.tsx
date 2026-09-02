import React, { useState } from 'react';
import {
  Settings,
  Cpu,
  Share2,
  Globe,
  Monitor,
  ShieldCheck,
  CheckCircle,
  Database,
  Server,
  Info,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';

export const SettingsPage: React.FC = () => {
  const { language, setLanguage } = useLanguage();
  const [aiProvider, setAiProvider] = useState<'mock' | 'gemini' | 'openai'>('mock');
  const [abdmMode, setAbdmMode] = useState<'mock' | 'sandbox'>('mock');
  const [kioskMode, setKioskMode] = useState(true);

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      <main className="flex-1 max-w-4xl mx-auto px-4 sm:px-6 py-8 w-full space-y-6">
        {/* Header */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm flex items-center gap-3.5">
          <div className="w-12 h-12 rounded-2xl bg-med-600 text-white flex items-center justify-center shadow-md shadow-med-600/20">
            <Settings className="w-7 h-7 stroke-[2.5]" />
          </div>
          <div>
            <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
              MediKiosk Platform & Integration Settings
            </h1>
            <p className="text-xs text-slate-500 font-medium">
              Architecture configurations, AI adapters, and ABDM/HIS sandbox controls
            </p>
          </div>
        </div>

        {/* Setting Card 1: AI Provider Abstraction */}
        <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center gap-2.5 pb-3 border-b border-slate-100">
            <Cpu className="w-5 h-5 text-med-600" />
            <div>
              <h3 className="text-sm font-extrabold text-slate-900">AI Intelligence Provider</h3>
              <p className="text-xs text-slate-500">Configurable abstraction interface (AIHistoryService & ClinicalSummaryService)</p>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            {[
              { id: 'mock', name: 'Mock Deterministic AI', desc: 'Zero external API dependencies. Ideal for offline hackathon judging.', badge: 'Active Default' },
              { id: 'gemini', name: 'Google Gemini 1.5 Pro', desc: 'Real LLM clinical dialogue & reasoning adapter.', badge: 'Integration Ready' },
              { id: 'openai', name: 'OpenAI GPT-4o', desc: 'Secondary multi-modal clinical reasoning engine.', badge: 'Integration Ready' },
            ].map((p) => (
              <button
                key={p.id}
                type="button"
                onClick={() => setAiProvider(p.id as any)}
                className={`text-left p-4 rounded-2xl border-2 transition-all ${
                  aiProvider === p.id
                    ? 'border-med-600 bg-med-50/50 shadow-xs'
                    : 'border-slate-200 hover:border-slate-300'
                }`}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="text-xs font-black text-slate-900">{p.name}</span>
                  {aiProvider === p.id && <CheckCircle className="w-4 h-4 text-med-600" />}
                </div>
                <p className="text-[11px] text-slate-500 leading-normal">{p.desc}</p>
                <span className="inline-block mt-2 px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-700">
                  {p.badge}
                </span>
              </button>
            ))}
          </div>
        </div>

        {/* Setting Card 2: ABDM / FHIR R4 Integration Mode */}
        <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center gap-2.5 pb-3 border-b border-slate-100">
            <Share2 className="w-5 h-5 text-teal-600" />
            <div>
              <h3 className="text-sm font-extrabold text-slate-900">ABDM & Hospital HIS Gateway</h3>
              <p className="text-xs text-slate-500">Ayushman Bharat Digital Mission compliant FHIR R4 document mapping</p>
            </div>
          </div>

          <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 text-xs space-y-2 text-slate-700">
            <div className="flex justify-between">
              <span className="text-slate-400">Health Facility ID:</span>
              <strong className="font-mono text-slate-900">IN-MH-AIIA-0021</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Facility Name:</span>
              <strong className="text-slate-900">All India Institute of Ayurveda, New Delhi</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">FHIR Profile:</span>
              <strong className="font-mono text-med-700">HL7 FHIR R4 DiagnosticReport & Composition</strong>
            </div>
            <div className="flex justify-between">
              <span className="text-slate-400">Current Mode:</span>
              <span className="px-2 py-0.5 rounded text-[10px] font-extrabold bg-emerald-100 text-emerald-800">
                Mock / Sandbox Integration Ready
              </span>
            </div>
          </div>
        </div>

        {/* Setting Card 3: Database & Backend Status */}
        <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm space-y-4">
          <div className="flex items-center gap-2.5 pb-3 border-b border-slate-100">
            <Database className="w-5 h-5 text-indigo-600" />
            <div>
              <h3 className="text-sm font-extrabold text-slate-900">Database & Deployment Profiles</h3>
              <p className="text-xs text-slate-500">Zero-setup embedded database and PostgreSQL container options</p>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
            <div className="p-4 rounded-2xl border border-slate-200 bg-slate-50">
              <h4 className="font-bold text-slate-900">Development Mode (Active)</h4>
              <p className="text-slate-500 text-[11px] mt-1">H2 In-Memory embedded database. Zero external setup required.</p>
              <span className="inline-block mt-2 text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                Connected & Ready
              </span>
            </div>

            <div className="p-4 rounded-2xl border border-slate-200 bg-slate-50">
              <h4 className="font-bold text-slate-900">Production Mode (Docker)</h4>
              <p className="text-slate-500 text-[11px] mt-1">PostgreSQL 16 via docker-compose.yml configuration.</p>
              <span className="inline-block mt-2 text-[10px] font-bold text-slate-600 bg-slate-200 px-2 py-0.5 rounded">
                Profile Available
              </span>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};
