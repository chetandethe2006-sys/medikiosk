import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  Activity,
  Globe,
  Stethoscope,
  AlertTriangle,
  Settings,
  Sparkles,
  HeartPulse,
  UserCheck,
} from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';
import { LanguageCode } from '../../types';

export const AppHeader: React.FC = () => {
  const { language, setLanguage, t } = useLanguage();
  const location = useLocation();
  const isDoctorRoute = location.pathname.startsWith('/doctor') || location.pathname.startsWith('/triage');

  return (
    <header className="sticky top-0 z-40 w-full bg-white/95 backdrop-blur border-b border-slate-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Branding */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-med-600 to-teal-500 flex items-center justify-center text-white shadow-md shadow-med-500/20 group-hover:scale-105 transition-transform">
              <Activity className="w-6 h-6 stroke-[2.5]" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-extrabold text-xl tracking-tight text-slate-900">MediKiosk</span>
                <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-med-100 text-med-800 border border-med-200">
                  AI Intake MVP
                </span>
              </div>
              <p className="text-[11px] font-medium text-slate-500 hidden sm:block">
                All India Institute of Ayurveda • Ministry of Ayush
              </p>
            </div>
          </Link>

          {/* Navigation Links */}
          <div className="flex items-center gap-2 sm:gap-4">
            <nav className="hidden md:flex items-center gap-1 bg-slate-100 p-1 rounded-xl border border-slate-200">
              <Link
                to="/patient/language"
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  !isDoctorRoute
                    ? 'bg-white text-med-700 shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <span className="flex items-center gap-1.5">
                  <UserCheck className="w-3.5 h-3.5" />
                  Patient Kiosk
                </span>
              </Link>
              <Link
                to="/doctor"
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                  location.pathname === '/doctor'
                    ? 'bg-white text-med-700 shadow-sm'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <span className="flex items-center gap-1.5">
                  <Stethoscope className="w-3.5 h-3.5" />
                  Doctor Dashboard
                </span>
              </Link>
            </nav>

            {/* Language Selector */}
            <div className="flex items-center bg-slate-50 border border-slate-200 rounded-lg p-1">
              <Globe className="w-3.5 h-3.5 text-slate-500 ml-1.5 mr-1" />
              {(['en', 'hi', 'mr'] as LanguageCode[]).map((lang) => (
                <button
                  key={lang}
                  onClick={() => setLanguage(lang)}
                  className={`px-2 py-1 text-xs font-semibold rounded transition-all ${
                    language === lang
                      ? 'bg-med-600 text-white shadow-xs'
                      : 'text-slate-600 hover:text-slate-900'
                  }`}
                >
                  {lang === 'en' ? 'EN' : lang === 'hi' ? 'हिन्दी' : 'मराठी'}
                </button>
              ))}
            </div>

            {/* Settings */}
            <Link
              to="/settings"
              title="System Settings & Integrations"
              className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors"
            >
              <Settings className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
};
