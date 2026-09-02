import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Globe, ArrowRight, Volume2, CheckCircle2 } from 'lucide-react';
import { useLanguage } from '../context/LanguageContext';
import { LanguageCode } from '../types';

export const LanguagePage: React.FC = () => {
  const { language, setLanguage, t, speakText } = useLanguage();
  const navigate = useNavigate();

  const languages = [
    {
      code: 'mr' as LanguageCode,
      nativeName: 'मराठी',
      englishName: 'Marathi',
      greeting: 'नमस्कार! तुमची भाषा निवडा.',
      subText: 'स्थानिक भाषेमध्ये सुलभ संवाद',
      badge: 'प्राधान्य भाषा (Maharashtra)',
    },
    {
      code: 'hi' as LanguageCode,
      nativeName: 'हिन्दी',
      englishName: 'Hindi',
      greeting: 'नमस्ते! अपनी भाषा चुनें।',
      subText: 'राष्ट्रीय भाषा में आसान बातचीत',
      badge: 'National Language',
    },
    {
      code: 'en' as LanguageCode,
      nativeName: 'English',
      englishName: 'English',
      greeting: 'Welcome! Choose English.',
      subText: 'Simple & clinical English intake',
      badge: 'Standard',
    },
  ];

  const handleSelectLanguage = (code: LanguageCode, greeting: string) => {
    setLanguage(code);
    speakText(greeting);
    setTimeout(() => {
      navigate('/patient/consent');
    }, 400);
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-gradient-to-b from-slate-50 to-slate-100 flex flex-col justify-center py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto w-full space-y-8">
        {/* Header Title */}
        <div className="text-center space-y-3">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-med-50 border border-med-200 text-med-800 text-xs font-extrabold shadow-xs">
            <Globe className="w-4 h-4 text-med-600" />
            <span>Kiosk Step 1 of 5</span>
          </div>
          <h1 className="text-2xl sm:text-4xl font-extrabold text-slate-950 tracking-tight">
            {t('language.title')}
          </h1>
          <p className="text-sm sm:text-base text-slate-600 font-medium max-w-lg mx-auto">
            {t('language.select_prompt')}
          </p>
        </div>

        {/* 3 Large Touch-Friendly Language Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          {languages.map((item) => {
            const isSelected = language === item.code;

            return (
              <button
                key={item.code}
                type="button"
                onClick={() => handleSelectLanguage(item.code, item.greeting)}
                className={`relative group text-left rounded-3xl p-6 transition-all duration-300 flex flex-col justify-between min-h-[220px] shadow-sm ${
                  isSelected
                    ? 'bg-gradient-to-br from-med-600 to-teal-600 text-white ring-4 ring-med-200 shadow-lg shadow-med-600/30 scale-[1.03]'
                    : 'bg-white hover:bg-slate-50/80 text-slate-900 border-2 border-slate-200 hover:border-med-400 hover:scale-[1.01]'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <span
                      className={`text-[10px] font-extrabold uppercase tracking-wider px-2 py-0.5 rounded ${
                        isSelected ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-600'
                      }`}
                    >
                      {item.badge}
                    </span>

                    {isSelected && (
                      <CheckCircle2 className="w-5 h-5 text-white" />
                    )}
                  </div>

                  <h2 className="text-2xl sm:text-3xl font-black tracking-tight mt-1">
                    {item.nativeName}
                  </h2>
                  <span
                    className={`text-xs font-semibold block mt-0.5 ${
                      isSelected ? 'text-med-100' : 'text-slate-500'
                    }`}
                  >
                    {item.englishName}
                  </span>

                  <p
                    className={`text-xs mt-3 leading-relaxed ${
                      isSelected ? 'text-white/90 font-medium' : 'text-slate-600'
                    }`}
                  >
                    {item.subText}
                  </p>
                </div>

                <div className="pt-4 flex items-center justify-between">
                  <span
                    className={`text-xs font-bold flex items-center gap-1.5 ${
                      isSelected ? 'text-white' : 'text-med-600 group-hover:text-med-700'
                    }`}
                  >
                    Select & Continue
                    <ArrowRight className="w-4 h-4" />
                  </span>

                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      speakText(item.greeting);
                    }}
                    title="Audio Preview"
                    className={`p-2 rounded-full transition-colors ${
                      isSelected ? 'bg-white/20 text-white hover:bg-white/30' : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                  >
                    <Volume2 className="w-4 h-4" />
                  </button>
                </div>
              </button>
            );
          })}
        </div>

        {/* Demo Patient Fast-track */}
        <div className="text-center pt-4">
          <p className="text-xs text-slate-500">
            Kiosk Interface conforms to Ayushman Bharat Digital Mission (ABDM) accessibility guidelines.
          </p>
        </div>
      </div>
    </div>
  );
};
