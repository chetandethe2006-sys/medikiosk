import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Activity,
  Send,
  Volume2,
  Sparkles,
  CheckCircle2,
  AlertCircle,
  Clock,
  Heart,
  ChevronRight,
  Shield,
  ArrowRight,
  Bot,
  User,
  Leaf,
  Layers,
  Thermometer,
} from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { useIntake } from '../context/IntakeContext';
import { ProgressStepper } from '../components/common/ProgressStepper';
import { VoiceInputButton } from '../components/common/VoiceInputButton';
import { RedFlagAlertModal } from '../components/common/RedFlagAlertModal';

export const ClinicalHistoryPage: React.FC = () => {
  const { t, language, speakText } = useLanguage();
  const {
    patient,
    session,
    history,
    currentStepData,
    startClinicalHistory,
    submitAnswer,
    activeRedFlag,
    dismissRedFlag,
    isAyushMode,
  } = useIntake();
  const navigate = useNavigate();

  const [textInput, setTextInput] = useState('');
  const [messages, setMessages] = useState<Array<{ sender: 'AI' | 'PATIENT'; text: string; mode?: string }>>([]);
  const [initialized, setInitialized] = useState(false);
  const chatEndRef = useRef<HTMLDivElement>(null);

  // Initialize interview if not started
  useEffect(() => {
    if (!initialized && session && patient) {
      setInitialized(true);
      const initialComplaint =
        language === 'hi'
          ? 'सीने में दर्द'
          : language === 'mr'
          ? 'छातीत दुखणे'
          : 'Chest pain since yesterday';

      // Initial AI greeting message
      const greeting =
        language === 'hi'
          ? 'नमस्ते! अखिल भारतीय आयुर्वेद संस्थान में आपका स्वागत है। आज आपको क्या परेशानी है?'
          : language === 'mr'
          ? 'नमस्कार! अखिल भारतीय आयुर्वेद संस्थान मध्ये आपले स्वागत आहे. आज आपल्याला कोणता त्रास होत आहे?'
          : "Hello! Welcome to the All India Institute of Ayurveda. What brings you to the hospital today?";

      setMessages([{ sender: 'AI', text: greeting }]);
      speakText(greeting);

      startClinicalHistory(initialComplaint);
    }
  }, [initialized, session, patient, language]);

  const lastSpokenRef = useRef<string>('');

  // Update messages when new question arrives
  useEffect(() => {
    if (currentStepData && currentStepData.questionText) {
      if (lastSpokenRef.current !== currentStepData.questionText) {
        lastSpokenRef.current = currentStepData.questionText;
        setMessages((prev) => {
          const lastMsg = prev[prev.length - 1];
          if (!lastMsg || lastMsg.text !== currentStepData.questionText) {
            return [...prev, { sender: 'AI', text: currentStepData.questionText }];
          }
          return prev;
        });
        if (currentStepData.speechPrompt) {
          speakText(currentStepData.speechPrompt);
        }
      }
    }
  }, [currentStepData, speakText]);

  // Auto-scroll chat
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSelectOption = async (option: string) => {
    setMessages((prev) => [...prev, { sender: 'PATIENT', text: option, mode: 'TAP' }]);
    await submitAnswer(option, 'TAP');
  };

  const handleSendText = async () => {
    if (!textInput.trim()) return;
    const ans = textInput.trim();
    setTextInput('');
    setMessages((prev) => [...prev, { sender: 'PATIENT', text: ans, mode: 'TEXT' }]);
    await submitAnswer(ans, 'TEXT');
  };

  const handleVoiceTranscript = async (transcript: string) => {
    setMessages((prev) => [...prev, { sender: 'PATIENT', text: transcript, mode: 'VOICE' }]);
    await submitAnswer(transcript, 'VOICE');
  };

  const handleProceedToDocuments = () => {
    navigate('/patient/documents');
  };

  const completeness = currentStepData?.completenessPercentage || (history ? 65 : 15);
  const isCompleted = currentStepData?.isCompleted || completeness >= 90;

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-100 flex flex-col">
      {/* 5-Step Stepper */}
      <ProgressStepper currentStep={2} completeness={completeness} />

      {/* Red Flag Alert Modal if triggered */}
      <RedFlagAlertModal alert={activeRedFlag} onDismiss={dismissRedFlag} />

      {/* 3-Column Clinical Intake Layout */}
      <main className="flex-1 max-w-7xl mx-auto px-3 sm:px-6 py-4 sm:py-6 w-full grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* LEFT COLUMN: Patient Context & Progress (3 Cols) */}
        <aside className="lg:col-span-3 space-y-4 order-2 lg:order-1">
          {/* Patient Card */}
          <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200 shadow-xs space-y-3">
            <div className="flex items-center gap-3 pb-3 border-b border-slate-100">
              <div className="w-10 h-10 rounded-xl bg-med-100 text-med-700 flex items-center justify-center font-bold text-sm">
                {patient?.gender === 'Female' ? 'F' : 'M'}
              </div>
              <div>
                <h3 className="text-sm font-extrabold text-slate-900">{patient?.fullName || 'Sunita Patil'}</h3>
                <p className="text-xs text-slate-500 font-medium">
                  {patient?.age || 52} yrs • {patient?.gender || 'Female'} • Token {session?.tokenNumber || '#104'}
                </p>
              </div>
            </div>

            <div className="text-xs space-y-1.5 text-slate-600">
              <div className="flex justify-between">
                <span className="text-slate-400">ABHA Health ID:</span>
                <span className="font-mono font-bold text-slate-800">{patient?.abhaId || '91-4567-8901-2345'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Intake Mode:</span>
                <span className="font-bold text-med-700">{isAyushMode ? 'Integrative AYUSH' : 'Modern OPD'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Language:</span>
                <span className="font-bold uppercase text-slate-700">{language}</span>
              </div>
            </div>
          </div>

          {/* Completeness Meter */}
          <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200 shadow-xs space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500">
                History Completeness
              </h4>
              <span className="text-sm font-black text-med-700">{completeness}%</span>
            </div>

            <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
              <div
                className="bg-gradient-to-r from-med-500 to-emerald-500 h-full rounded-full transition-all duration-500"
                style={{ width: `${completeness}%` }}
              />
            </div>
            <p className="text-[11px] text-slate-400 leading-tight">
              Measures required clinical data points captured for physician review.
            </p>
          </div>

          {/* AYUSH Indicator */}
          {isAyushMode && (
            <div className="bg-gradient-to-br from-ayush-50 to-white rounded-2xl p-4 border border-ayush-200 shadow-xs space-y-2">
              <div className="flex items-center gap-2 text-ayush-900 font-extrabold text-xs">
                <Leaf className="w-4 h-4 text-ayush-600" />
                <span>AYUSH Protocol Active</span>
              </div>
              <p className="text-[11px] text-ayush-700 leading-relaxed">
                Capturing Prakriti, Agni (digestion), Ahara-Vihara lifestyle habits for integrative AIIA OPD.
              </p>
            </div>
          )}
        </aside>

        {/* CENTER COLUMN: Conversational AI Dialogue (6 Cols) */}
        <section className="lg:col-span-6 flex flex-col bg-white rounded-3xl border border-slate-200 shadow-lg overflow-hidden order-1 lg:order-2 min-h-[550px]">
          {/* Chat Top Bar */}
          <div className="px-5 py-3.5 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-xl bg-med-600 text-white flex items-center justify-center shadow-xs">
                <Bot className="w-4 h-4" />
              </div>
              <div>
                <h3 className="text-xs font-extrabold text-slate-900">MediKiosk Clinical AI Guide</h3>
                <p className="text-[10px] font-semibold text-emerald-600 flex items-center gap-1">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                  Online • Adaptive Clinical Branching
                </p>
              </div>
            </div>

            <button
              type="button"
              onClick={() => currentStepData?.speechPrompt && speakText(currentStepData.speechPrompt)}
              className="p-2 text-slate-500 hover:text-med-700 hover:bg-slate-200 rounded-lg transition-colors"
              title="Repeat Audio Prompt"
            >
              <Volume2 className="w-4 h-4" />
            </button>
          </div>

          {/* Message Stream */}
          <div className="flex-1 p-4 sm:p-6 overflow-y-auto space-y-4">
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`flex gap-3 ${msg.sender === 'PATIENT' ? 'justify-end' : 'justify-start'}`}
              >
                {msg.sender === 'AI' && (
                  <div className="w-8 h-8 rounded-xl bg-med-600 text-white flex items-center justify-center flex-shrink-0 shadow-xs mt-1">
                    <Bot className="w-4 h-4" />
                  </div>
                )}

                <div
                  className={`max-w-[82%] rounded-2xl px-4 py-3 text-xs sm:text-sm leading-relaxed shadow-xs ${
                    msg.sender === 'PATIENT'
                      ? 'bg-med-600 text-white font-medium rounded-br-xs'
                      : 'bg-slate-100 text-slate-900 font-semibold border border-slate-200/80 rounded-bl-xs'
                  }`}
                >
                  <p>{msg.text}</p>
                  {msg.mode && (
                    <span className="text-[9px] uppercase font-bold text-med-200 mt-1 block">
                      via {msg.mode}
                    </span>
                  )}
                </div>

                {msg.sender === 'PATIENT' && (
                  <div className="w-8 h-8 rounded-xl bg-slate-200 text-slate-700 flex items-center justify-center flex-shrink-0 shadow-xs mt-1">
                    <User className="w-4 h-4" />
                  </div>
                )}
              </div>
            ))}
            <div ref={chatEndRef} />
          </div>

          {/* Interactive Response Area */}
          <div className="p-4 bg-slate-50 border-t border-slate-200 space-y-4">
            {/* Quick Touch Suggestion Chips */}
            {currentStepData && currentStepData.quickOptions && currentStepData.quickOptions.length > 0 && !isCompleted && (
              <div>
                <span className="text-[10px] font-extrabold uppercase tracking-wider text-slate-400 block mb-2">
                  {t('history.tap_hint')}
                </span>
                <div className="flex flex-wrap gap-2">
                  {currentStepData.quickOptions.map((opt, i) => (
                    <button
                      key={i}
                      type="button"
                      onClick={() => handleSelectOption(opt)}
                      className="px-3.5 py-2.5 bg-white hover:bg-med-50 text-slate-800 hover:text-med-800 border-2 border-slate-200 hover:border-med-400 rounded-xl text-xs font-bold transition-all shadow-2xs hover:scale-[1.02] active:scale-95 text-left"
                    >
                      {opt}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Dual Input Area: Voice Microphone + Text Bar */}
            {!isCompleted ? (
              <div className="pt-2 flex flex-col sm:flex-row items-center gap-4">
                <VoiceInputButton onTranscript={handleVoiceTranscript} />

                <div className="flex-1 w-full flex items-center gap-2">
                  <input
                    type="text"
                    value={textInput}
                    onChange={(e) => setTextInput(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleSendText()}
                    placeholder={t('history.type_placeholder')}
                    className="flex-1 px-4 py-3 rounded-xl border border-slate-300 text-xs font-medium focus:ring-2 focus:ring-med-500 bg-white"
                  />
                  <button
                    type="button"
                    onClick={handleSendText}
                    className="p-3 bg-med-600 hover:bg-med-700 text-white rounded-xl shadow-sm transition-colors"
                  >
                    <Send className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ) : (
              <div className="bg-emerald-50 p-4 rounded-2xl border border-emerald-200 text-center space-y-3">
                <div className="flex items-center justify-center gap-2 text-emerald-800 font-extrabold text-sm">
                  <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                  <span>Clinical History Captured Successfully</span>
                </div>
                <button
                  type="button"
                  onClick={handleProceedToDocuments}
                  className="w-full py-3.5 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-extrabold text-sm rounded-xl shadow-md transition-all flex items-center justify-center gap-2"
                >
                  <span>Proceed to Document Upload</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            )}
          </div>
        </section>

        {/* RIGHT COLUMN: Live Captured Structured State (3 Cols) */}
        <aside className="lg:col-span-3 space-y-4 order-3">
          <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200 shadow-xs space-y-3.5">
            <div className="flex items-center justify-between pb-2 border-b border-slate-100">
              <h4 className="text-xs font-extrabold text-slate-900 uppercase tracking-wide flex items-center gap-1.5">
                <Activity className="w-4 h-4 text-med-600" />
                {t('history.captured_title')}
              </h4>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-med-50 text-med-700">
                Live Sync
              </span>
            </div>

            {/* Structured Point 1: Chief Complaint */}
            <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80">
              <span className="text-[10px] uppercase font-bold text-slate-400 block">{t('history.chief_complaint')}</span>
              <p className="text-xs font-bold text-slate-800 mt-0.5">
                {history?.chiefComplaint || 'Chest pain since yesterday'}
              </p>
            </div>

            {/* Structured Point 2: Onset & Duration */}
            {history?.onsetAndDuration && (
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">{t('history.onset')}</span>
                <p className="text-xs font-medium text-slate-800 mt-0.5">{history.onsetAndDuration}</p>
              </div>
            )}

            {/* Structured Point 3: Location & Radiation */}
            {history?.painLocation && (
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">{t('history.location')}</span>
                <p className="text-xs font-medium text-slate-800 mt-0.5">{history.painLocation}</p>
              </div>
            )}

            {/* Structured Point 4: Severity Scale */}
            {history?.severityScale !== undefined && (
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">{t('history.severity')}</span>
                <div className="flex items-center gap-2 mt-1">
                  <span className="text-sm font-extrabold text-red-600">{history.severityScale} / 10</span>
                  <div className="flex-1 bg-slate-200 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-red-500 h-full rounded-full"
                      style={{ width: `${(history.severityScale / 10) * 100}%` }}
                    />
                  </div>
                </div>
              </div>
            )}

            {/* Structured Point 5: Associated Symptoms */}
            {history?.associatedSymptoms && (
              <div className="bg-slate-50 p-3 rounded-xl border border-slate-200/80">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">{t('history.associated')}</span>
                <p className="text-xs font-medium text-slate-800 mt-0.5">{history.associatedSymptoms}</p>
              </div>
            )}

            {/* Structured Point 6: AYUSH Assessment */}
            {isAyushMode && history?.prakriti && (
              <div className="bg-ayush-50 p-3 rounded-xl border border-ayush-200">
                <span className="text-[10px] uppercase font-bold text-ayush-800 block">AYUSH Prakriti</span>
                <p className="text-xs font-bold text-ayush-950 mt-0.5">{history.prakriti}</p>
              </div>
            )}
          </div>
        </aside>
      </main>
    </div>
  );
};
