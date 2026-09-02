import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  User,
  QrCode,
  Sparkles,
  ArrowRight,
  Leaf,
  CheckCircle2,
  Phone,
  MapPin,
  Heart,
  Shield,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';
import { ProgressStepper } from '../components/common/ProgressStepper';

export const PatientIdentityPage: React.FC = () => {
  const { t, language } = useLanguage();
  const { registerPatient, startSession, isAyushMode, toggleAyushMode, loadDemoPatient } = useIntake();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<'ABHA' | 'WALKIN'>('ABHA');
  const [formData, setFormData] = useState({
    abhaId: '91-4567-8901-2345',
    fullName: 'Sunita Patil',
    age: 52,
    gender: 'Female',
    phone: '+91 98201 45678',
    address: 'Dadar, Mumbai - 400014',
    emergencyContact: '+91 98201 45679',
    bloodGroup: 'B+',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'age' ? parseInt(value, 10) || 0 : value,
    }));
  };

  const handleUseDemo = async () => {
    setIsSubmitting(true);
    try {
      await loadDemoPatient('SUNITA');
      navigate('/patient/history');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const patient = await registerPatient(formData);
      const session = await startSession(patient.id);
      navigate('/patient/history');
    } catch (err) {
      console.error('Registration error:', err);
      navigate('/patient/history');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 flex flex-col">
      <ProgressStepper currentStep={1} completeness={25} />

      <main className="flex-1 max-w-4xl mx-auto px-4 py-8 sm:py-12 w-full space-y-6">
        {/* Top Fast-Track Demo Patient Banner */}
        <div className="bg-gradient-to-r from-med-600 to-teal-600 rounded-3xl p-5 sm:p-6 text-white shadow-xl flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-2xl bg-white/20 flex items-center justify-center flex-shrink-0">
              <Sparkles className="w-6 h-6 text-white" />
            </div>
            <div>
              <span className="text-[10px] font-extrabold uppercase tracking-widest bg-white/25 px-2 py-0.5 rounded">
                One-Click Hackathon Demo
              </span>
              <h3 className="text-lg font-black mt-0.5">Use Recommended Demo Patient</h3>
              <p className="text-xs text-med-100 font-medium">
                Sunita Patil (52F) • Chest Pain • Red-Flag Triage • Lab Report Extraction • AYUSH Mode
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={handleUseDemo}
            disabled={isSubmitting}
            className="w-full sm:w-auto px-6 py-3 bg-white text-med-800 hover:bg-med-50 font-extrabold text-xs sm:text-sm rounded-xl shadow-md transition-all hover:scale-105 active:scale-95 flex items-center justify-center gap-2 flex-shrink-0"
          >
            <span>Launch Sunita Patil Case</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {/* Identity Form Card */}
        <div className="bg-white rounded-3xl p-6 sm:p-10 border border-slate-200 shadow-xl shadow-slate-100 space-y-8">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
            <div>
              <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                {t('identity.title')}
              </h1>
              <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                {t('identity.subtitle')}
              </p>
            </div>

            {/* Mode Tabs */}
            <div className="flex items-center bg-slate-100 p-1 rounded-xl border border-slate-200 self-start sm:self-auto">
              <button
                type="button"
                onClick={() => setActiveTab('ABHA')}
                className={`px-4 py-2 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  activeTab === 'ABHA' ? 'bg-white text-med-700 shadow-sm' : 'text-slate-600'
                }`}
              >
                <QrCode className="w-3.5 h-3.5" />
                {t('identity.tab_abha')}
              </button>
              <button
                type="button"
                onClick={() => setActiveTab('WALKIN')}
                className={`px-4 py-2 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
                  activeTab === 'WALKIN' ? 'bg-white text-med-700 shadow-sm' : 'text-slate-600'
                }`}
              >
                <User className="w-3.5 h-3.5" />
                {t('identity.tab_new')}
              </button>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {activeTab === 'ABHA' && (
              <div className="bg-med-50/70 p-5 rounded-2xl border border-med-200 space-y-2">
                <label className="block text-xs font-bold text-med-900 uppercase tracking-wide">
                  {t('identity.abha_input')}
                </label>
                <div className="relative">
                  <input
                    type="text"
                    name="abhaId"
                    value={formData.abhaId}
                    onChange={handleChange}
                    placeholder="91-XXXX-XXXX-XXXX"
                    className="w-full px-4 py-3 rounded-xl border border-med-300 bg-white font-mono text-sm font-bold text-slate-900 focus:ring-2 focus:ring-med-500"
                  />
                  <span className="absolute right-3 top-3 text-[11px] font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                    ABHA Verified
                  </span>
                </div>
              </div>
            )}

            {/* Demographics Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wide mb-1">
                  {t('identity.name')} *
                </label>
                <input
                  type="text"
                  name="fullName"
                  required
                  value={formData.fullName}
                  onChange={handleChange}
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 font-medium text-sm focus:ring-2 focus:ring-med-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase tracking-wide mb-1">
                    {t('identity.age')} *
                  </label>
                  <input
                    type="number"
                    name="age"
                    min="1"
                    max="120"
                    required
                    value={formData.age}
                    onChange={handleChange}
                    className="w-full px-4 py-3 rounded-xl border border-slate-300 font-medium text-sm focus:ring-2 focus:ring-med-500"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-700 uppercase tracking-wide mb-1">
                    {t('identity.gender')} *
                  </label>
                  <select
                    name="gender"
                    value={formData.gender}
                    onChange={handleChange}
                    className="w-full px-3 py-3 rounded-xl border border-slate-300 font-medium text-sm focus:ring-2 focus:ring-med-500 bg-white"
                  >
                    <option value="Female">{t('identity.gender_female')}</option>
                    <option value="Male">{t('identity.gender_male')}</option>
                    <option value="Other">{t('identity.gender_other')}</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wide mb-1">
                  {t('identity.phone')}
                </label>
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 font-medium text-sm focus:ring-2 focus:ring-med-500"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wide mb-1">
                  {t('identity.emergency_contact')}
                </label>
                <input
                  type="text"
                  name="emergencyContact"
                  value={formData.emergencyContact}
                  onChange={handleChange}
                  className="w-full px-4 py-3 rounded-xl border border-slate-300 font-medium text-sm focus:ring-2 focus:ring-med-500"
                />
              </div>
            </div>

            {/* AYUSH Assessment Mode Toggle */}
            <div className="bg-gradient-to-r from-ayush-50 to-amber-50 p-5 rounded-2xl border border-ayush-200 flex items-center justify-between gap-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-ayush-600 text-white flex items-center justify-center flex-shrink-0 shadow-sm">
                  <Leaf className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-xs font-extrabold text-ayush-900 uppercase tracking-wide">
                    {t('identity.ayush_mode_toggle')}
                  </h4>
                  <p className="text-xs text-ayush-700 mt-0.5">
                    {t('identity.ayush_mode_desc')}
                  </p>
                </div>
              </div>

              <button
                type="button"
                onClick={toggleAyushMode}
                className={`relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none ${
                  isAyushMode ? 'bg-ayush-600' : 'bg-slate-300'
                }`}
              >
                <span
                  className={`inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                    isAyushMode ? 'translate-x-5' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>

            {/* Submit Button */}
            <div className="pt-4 flex justify-end">
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full sm:w-auto px-8 py-4 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-extrabold text-base rounded-2xl shadow-lg shadow-med-600/30 hover:scale-[1.02] active:scale-[0.98] transition-all flex items-center justify-center gap-2"
              >
                <span>{t('identity.proceed_button')}</span>
                <ArrowRight className="w-5 h-5" />
              </button>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
};
