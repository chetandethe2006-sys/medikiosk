import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  HeartPulse, 
  Phone, 
  Hash, 
  CreditCard,
  ArrowRight,
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
  Loader2,
  Globe2
} from 'lucide-react';
import { useIntake } from '../context/IntakeContext';

type Tab = 'ABHA' | 'MOBILE' | 'TOKEN';

export const PatientLoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { loadDemoPatient, patient } = useIntake();
  
  const [activeTab, setActiveTab] = useState<Tab>('MOBILE');
  
  // Form states
  const [abhaId, setAbhaId] = useState('');
  const [mobile, setMobile] = useState('');
  const [otp, setOtp] = useState('');
  const [token, setToken] = useState('');
  
  // UI states
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [isVerified, setIsVerified] = useState(false);

  // Clear errors when switching tabs
  useEffect(() => {
    setError('');
    setIsOtpSent(false);
    setOtp('');
  }, [activeTab]);

  const handleSendOtp = () => {
    if (mobile.replace(/\D/g, '').length !== 10) {
      setError('Please enter a valid 10-digit mobile number.');
      return;
    }
    setError('');
    setIsLoading(true);
    // Simulate sending OTP
    setTimeout(() => {
      setIsLoading(false);
      setIsOtpSent(true);
    }, 1000);
  };

  const handleVerify = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setError('');
    
    // Validation
    if (activeTab === 'ABHA' && abhaId.replace(/[^0-9]/g, '').length !== 14) {
      setError('ABHA ID must be 14 digits.');
      return;
    }
    if (activeTab === 'MOBILE' && otp.length < 4) {
      setError('Please enter the OTP sent to your mobile.');
      return;
    }
    if (activeTab === 'TOKEN' && token.length < 3) {
      setError('Please enter a valid token number.');
      return;
    }

    setIsLoading(true);
    
    try {
      // Simulate verification and data fetch
      await loadDemoPatient('SUNITA');
      setIsVerified(true);
      
      // Auto transition after 3 seconds
      setTimeout(() => {
        navigate('/patient/language');
      }, 3000);
    } catch (err) {
      setError('Verification failed. Please try again or contact the reception desk.');
    } finally {
      setIsLoading(false);
    }
  };

  if (isVerified && patient) {
    return (
      <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
        <div className="w-full max-w-md bg-white rounded-3xl p-8 border border-emerald-200 shadow-2xl shadow-emerald-900/5 text-center transform transition-all animate-in fade-in zoom-in duration-500">
          <div className="w-20 h-20 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle2 className="w-10 h-10" />
          </div>
          <h2 className="text-2xl font-black text-slate-900 mb-2">Identity Verified</h2>
          <p className="text-slate-500 text-sm mb-6">Welcome to the OPD Kiosk.</p>
          
          <div className="bg-slate-50 rounded-2xl p-5 border border-slate-100 space-y-3 mb-8 text-left">
            <div>
              <span className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Patient Name</span>
              <strong className="text-lg text-slate-900">{patient.fullName}</strong>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <span className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Age / Gender</span>
                <span className="font-semibold text-slate-700">{patient.age}y / {patient.gender}</span>
              </div>
              <div>
                <span className="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-1">Intake Mode</span>
                <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-indigo-100 text-indigo-700 text-xs font-bold">
                  <ShieldCheck className="w-3.5 h-3.5" /> AI Assisted
                </span>
              </div>
            </div>
          </div>
          
          <div className="flex items-center justify-center gap-3 text-med-600 font-bold animate-pulse">
            <span>Starting clinical intake</span>
            <Loader2 className="w-5 h-5 animate-spin" />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
      {/* Top Navigation Bar */}
      <header className="w-full bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-med-600 flex items-center justify-center shadow-lg shadow-med-600/20">
            <HeartPulse className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-black text-slate-900 tracking-tight leading-none">MediKiosk</h1>
            <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mt-1">Patient Portal</p>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2 px-3 py-1.5 bg-slate-100 rounded-full text-sm font-semibold text-slate-600">
            <Globe2 className="w-4 h-4 text-slate-400" />
            <span className="text-med-700 font-bold">EN</span>
            <span className="text-slate-300">|</span>
            <span className="hover:text-slate-900 cursor-pointer">हिन्दी</span>
            <span className="text-slate-300">|</span>
            <span className="hover:text-slate-900 cursor-pointer">मराठी</span>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 flex items-center justify-center p-6">
        <div className="w-full max-w-lg bg-white rounded-[2rem] p-8 sm:p-10 border border-slate-200 shadow-2xl shadow-slate-900/5">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-black text-slate-900 tracking-tight">Identify Yourself</h2>
            <p className="text-slate-500 font-medium mt-2 text-sm sm:text-base">
              Please provide your identification to start the clinical intake process.
            </p>
          </div>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 flex items-start gap-3 animate-in slide-in-from-top-2">
              <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
              <span className="text-sm font-semibold">{error}</span>
            </div>
          )}

          {/* Identification Tabs */}
          <div className="flex p-1.5 bg-slate-100 rounded-2xl mb-8">
            <button
              onClick={() => setActiveTab('MOBILE')}
              className={`flex-1 py-3 text-sm font-bold rounded-xl flex items-center justify-center gap-2 transition-all ${
                activeTab === 'MOBILE' 
                  ? 'bg-white text-med-700 shadow-sm' 
                  : 'text-slate-500 hover:text-slate-700 hover:bg-slate-200/50'
              }`}
            >
              <Phone className="w-4 h-4" />
              <span className="hidden sm:inline">Mobile Number</span>
              <span className="sm:hidden">Mobile</span>
            </button>
            <button
              onClick={() => setActiveTab('ABHA')}
              className={`flex-1 py-3 text-sm font-bold rounded-xl flex items-center justify-center gap-2 transition-all ${
                activeTab === 'ABHA' 
                  ? 'bg-white text-med-700 shadow-sm' 
                  : 'text-slate-500 hover:text-slate-700 hover:bg-slate-200/50'
              }`}
            >
              <CreditCard className="w-4 h-4" />
              <span>ABHA ID</span>
            </button>
            <button
              onClick={() => setActiveTab('TOKEN')}
              className={`flex-1 py-3 text-sm font-bold rounded-xl flex items-center justify-center gap-2 transition-all ${
                activeTab === 'TOKEN' 
                  ? 'bg-white text-med-700 shadow-sm' 
                  : 'text-slate-500 hover:text-slate-700 hover:bg-slate-200/50'
              }`}
            >
              <Hash className="w-4 h-4" />
              <span>Token</span>
            </button>
          </div>

          <form onSubmit={handleVerify} className="space-y-6">
            {/* MOBILE TAB */}
            {activeTab === 'MOBILE' && (
              <div className="space-y-5 animate-in fade-in duration-300">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-2">10-Digit Mobile Number</label>
                  <div className="relative flex">
                    <span className="inline-flex items-center px-4 rounded-l-2xl border border-r-0 border-slate-300 bg-slate-50 text-slate-500 font-bold sm:text-lg">
                      +91
                    </span>
                    <input
                      type="tel"
                      maxLength={10}
                      disabled={isOtpSent || isLoading}
                      className="flex-1 w-full px-5 py-4 rounded-r-2xl border border-slate-300 text-lg sm:text-xl font-bold text-slate-900 tracking-wider focus:ring-4 focus:ring-med-500/20 focus:border-med-500 disabled:bg-slate-50 disabled:text-slate-400 outline-none transition-all"
                      placeholder="98765 43210"
                      value={mobile}
                      onChange={(e) => setMobile(e.target.value.replace(/\D/g, ''))}
                    />
                  </div>
                </div>

                {!isOtpSent ? (
                  <button
                    type="button"
                    onClick={handleSendOtp}
                    disabled={mobile.length !== 10 || isLoading}
                    className="w-full py-4 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-200 disabled:text-slate-400 text-white text-lg font-bold rounded-2xl transition-colors flex items-center justify-center gap-2"
                  >
                    {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : 'Send Verification OTP'}
                  </button>
                ) : (
                  <div className="space-y-5 animate-in slide-in-from-bottom-2">
                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <label className="block text-sm font-bold text-slate-700">Enter 4-Digit OTP</label>
                        <button 
                          type="button" 
                          onClick={() => { setIsOtpSent(false); setOtp(''); }}
                          className="text-xs font-bold text-med-600 hover:text-med-700"
                        >
                          Change Number
                        </button>
                      </div>
                      <input
                        type="text"
                        maxLength={4}
                        disabled={isLoading}
                        className="w-full px-5 py-4 rounded-2xl border border-slate-300 text-2xl font-black text-center tracking-[0.5em] text-slate-900 focus:ring-4 focus:ring-med-500/20 focus:border-med-500 outline-none transition-all"
                        placeholder="••••"
                        value={otp}
                        onChange={(e) => setOtp(e.target.value.replace(/\D/g, ''))}
                      />
                    </div>
                    <button
                      type="submit"
                      disabled={otp.length !== 4 || isLoading}
                      className="w-full py-4 bg-med-600 hover:bg-med-700 disabled:bg-slate-200 disabled:text-slate-400 text-white text-lg font-bold rounded-2xl transition-all shadow-lg shadow-med-600/30 flex items-center justify-center gap-2"
                    >
                      {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : (
                        <>Verify Identity <ArrowRight className="w-6 h-6" /></>
                      )}
                    </button>
                  </div>
                )}
              </div>
            )}

            {/* ABHA TAB */}
            {activeTab === 'ABHA' && (
              <div className="space-y-6 animate-in fade-in duration-300">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-2">14-Digit ABHA ID</label>
                  <input
                    type="text"
                    disabled={isLoading}
                    className="w-full px-5 py-4 rounded-2xl border border-slate-300 text-lg sm:text-xl font-bold text-slate-900 tracking-[0.1em] focus:ring-4 focus:ring-med-500/20 focus:border-med-500 outline-none transition-all text-center"
                    placeholder="12-3456-7890-1234"
                    value={abhaId}
                    onChange={(e) => {
                      // auto format xx-xxxx-xxxx-xxxx
                      let val = e.target.value.replace(/\D/g, '');
                      if (val.length > 14) val = val.substring(0, 14);
                      const parts = [];
                      if (val.length > 0) parts.push(val.substring(0, 2));
                      if (val.length > 2) parts.push(val.substring(2, 6));
                      if (val.length > 6) parts.push(val.substring(6, 10));
                      if (val.length > 10) parts.push(val.substring(10, 14));
                      setAbhaId(parts.join('-'));
                    }}
                  />
                  <p className="text-xs font-medium text-slate-500 text-center mt-3">
                    Ayushman Bharat Health Account Number
                  </p>
                </div>
                <button
                  type="submit"
                  disabled={abhaId.replace(/\D/g, '').length !== 14 || isLoading}
                  className="w-full py-4 bg-med-600 hover:bg-med-700 disabled:bg-slate-200 disabled:text-slate-400 text-white text-lg font-bold rounded-2xl transition-all shadow-lg shadow-med-600/30 flex items-center justify-center gap-2"
                >
                  {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : (
                    <>Verify ABHA ID <ArrowRight className="w-6 h-6" /></>
                  )}
                </button>
              </div>
            )}

            {/* TOKEN TAB */}
            {activeTab === 'TOKEN' && (
              <div className="space-y-6 animate-in fade-in duration-300">
                <div>
                  <label className="block text-sm font-bold text-slate-700 mb-2">OPD Token / Appointment Number</label>
                  <input
                    type="text"
                    disabled={isLoading}
                    className="w-full px-5 py-4 rounded-2xl border border-slate-300 text-2xl font-black text-slate-900 tracking-wider focus:ring-4 focus:ring-med-500/20 focus:border-med-500 outline-none transition-all uppercase text-center"
                    placeholder="e.g. T-479"
                    value={token}
                    onChange={(e) => setToken(e.target.value.toUpperCase())}
                  />
                </div>
                <button
                  type="submit"
                  disabled={token.length < 3 || isLoading}
                  className="w-full py-4 bg-med-600 hover:bg-med-700 disabled:bg-slate-200 disabled:text-slate-400 text-white text-lg font-bold rounded-2xl transition-all shadow-lg shadow-med-600/30 flex items-center justify-center gap-2"
                >
                  {isLoading ? <Loader2 className="w-6 h-6 animate-spin" /> : (
                    <>Start Intake <ArrowRight className="w-6 h-6" /></>
                  )}
                </button>
              </div>
            )}
          </form>

          {/* Support Link */}
          <div className="mt-8 pt-6 border-t border-slate-100 text-center">
            <p className="text-sm font-semibold text-slate-500">
              Need help? <button type="button" className="text-med-600 hover:text-med-700">Ask the Reception Desk</button>
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};
