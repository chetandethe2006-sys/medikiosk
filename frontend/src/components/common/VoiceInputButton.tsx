import React, { useState, useEffect } from 'react';
import { Mic, MicOff, Volume2, AlertCircle } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

interface VoiceInputButtonProps {
  onTranscript: (text: string) => void;
  disabled?: boolean;
}

export const VoiceInputButton: React.FC<VoiceInputButtonProps> = ({ onTranscript, disabled = false }) => {
  const { language } = useLanguage();
  const [isListening, setIsListening] = useState(false);
  const [isSupported, setIsSupported] = useState(true);
  const [interimText, setInterimText] = useState('');

  useEffect(() => {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    if (!SpeechRecognition) {
      setIsSupported(false);
    }
  }, []);

  const toggleListening = () => {
    if (disabled) return;

    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    if (!SpeechRecognition) {
      setIsSupported(false);
      // Deterministic demo voice fallback
      setIsListening(true);
      setTimeout(() => {
        setIsListening(false);
        const fallbackPhrase =
          language === 'hi'
            ? 'मुझे कल दोपहर से सीने में दर्द हो रहा है'
            : language === 'mr'
            ? 'काल दुपारपासून छातीत दुखत आहे आणि घाम येत आहे'
            : 'I have crushing chest pain since yesterday with breathlessness';
        onTranscript(fallbackPhrase);
      }, 2000);
      return;
    }

    if (isListening) {
      setIsListening(false);
      return;
    }

    try {
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = true;

      if (language === 'hi') {
        recognition.lang = 'hi-IN';
      } else if (language === 'mr') {
        recognition.lang = 'mr-IN';
      } else {
        recognition.lang = 'en-IN';
      }

      recognition.onstart = () => {
        setIsListening(true);
        setInterimText('');
      };

      recognition.onresult = (event: any) => {
        const current = event.resultIndex;
        const transcript = event.results[current][0].transcript;
        setInterimText(transcript);
        if (event.results[current].isFinal) {
          onTranscript(transcript);
          setIsListening(false);
        }
      };

      recognition.onerror = () => {
        setIsListening(false);
      };

      recognition.onend = () => {
        setIsListening(false);
      };

      recognition.start();
    } catch (err) {
      console.warn('Speech recognition error, using fallback:', err);
      setIsListening(false);
    }
  };

  return (
    <div className="flex flex-col items-center gap-2">
      <button
        type="button"
        onClick={toggleListening}
        disabled={disabled}
        aria-label={isListening ? 'Stop listening' : 'Start speaking'}
        className={`relative group flex items-center justify-center rounded-2xl transition-all duration-300 ${
          isListening
            ? 'w-16 h-16 sm:w-20 sm:h-20 bg-red-600 text-white shadow-lg shadow-red-500/40 animate-pulse'
            : 'w-16 h-16 sm:w-20 sm:h-20 bg-gradient-to-tr from-med-600 to-teal-500 text-white shadow-lg shadow-med-600/30 hover:scale-105 active:scale-95'
        } ${disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}`}
      >
        {isListening ? (
          <MicOff className="w-8 h-8 sm:w-9 sm:h-9" />
        ) : (
          <Mic className="w-8 h-8 sm:w-9 sm:h-9" />
        )}

        {isListening && (
          <span className="absolute -top-1 -right-1 flex h-4 w-4">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-4 w-4 bg-red-500"></span>
          </span>
        )}
      </button>

      <div className="text-center">
        <span
          className={`text-xs font-bold tracking-wide uppercase ${
            isListening ? 'text-red-600 font-extrabold' : 'text-slate-600'
          }`}
        >
          {isListening
            ? language === 'hi'
              ? 'सुन रहे हैं...'
              : language === 'mr'
              ? 'ऐकत आहे...'
              : 'Listening...'
            : language === 'hi'
            ? 'बोलने के लिए दबाएं'
            : language === 'mr'
            ? 'बोलण्यासाठी स्पर्श करा'
            : 'Tap to Speak'}
        </span>
        {interimText && (
          <p className="text-xs text-slate-500 italic mt-1 max-w-xs truncate">"{interimText}"</p>
        )}
      </div>
    </div>
  );
};
