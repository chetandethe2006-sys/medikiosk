import React from 'react';
import { AlertCircle, ArrowRight, Check, ShieldAlert } from 'lucide-react';
import { RedFlagEvent } from '../../types';
import { useNavigate } from 'react-router-dom';

interface RedFlagAlertModalProps {
  alert: RedFlagEvent | null;
  onDismiss: () => void;
}

export const RedFlagAlertModal: React.FC<RedFlagAlertModalProps> = ({ alert, onDismiss }) => {
  const navigate = useNavigate();
  if (!alert) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl border-2 border-red-500 max-w-lg w-full overflow-hidden animate-slide-up">
        {/* Top Warning Banner */}
        <div className="bg-gradient-to-r from-red-600 to-rose-600 text-white px-6 py-4 flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-white/20 flex items-center justify-center flex-shrink-0">
            <ShieldAlert className="w-6 h-6 stroke-[2.5]" />
          </div>
          <div>
            <span className="text-[11px] font-extrabold uppercase tracking-wider bg-white/25 px-2 py-0.5 rounded">
              Priority Clinical Alert
            </span>
            <h3 className="text-lg font-extrabold leading-tight mt-0.5">
              Potential Red Flag Detected
            </h3>
          </div>
        </div>

        {/* Body Content */}
        <div className="p-6 space-y-4">
          <div className="bg-red-50 border border-red-200 rounded-xl p-4">
            <h4 className="text-xs font-bold text-red-900 uppercase tracking-wide">
              Reported Symptom Cluster
            </h4>
            <p className="text-sm font-bold text-red-800 mt-1">
              {alert.symptomsReported}
            </p>
          </div>

          <div>
            <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wide">
              Clinical Recommendation
            </h4>
            <p className="text-sm text-slate-700 mt-1 leading-relaxed">
              {alert.clinicalRecommendation}
            </p>
          </div>

          <div className="bg-slate-50 p-3 rounded-lg border border-slate-200 text-xs text-slate-500">
            <span className="font-semibold text-slate-700">Safety Notice:</span> This system highlights potential risk patterns for physician attention. Clinical staff has been notified on the triage monitor.
          </div>

          {/* Action Buttons */}
          <div className="pt-2 flex flex-col sm:flex-row gap-2.5">
            <button
              type="button"
              onClick={onDismiss}
              className="flex-1 px-4 py-3 bg-red-600 hover:bg-red-700 text-white font-bold rounded-xl shadow-md transition-colors flex items-center justify-center gap-2"
            >
              <Check className="w-4 h-4" />
              Acknowledge & Continue
            </button>
            <button
              type="button"
              onClick={() => {
                onDismiss();
                navigate('/triage');
              }}
              className="px-4 py-3 bg-slate-100 hover:bg-slate-200 text-slate-800 font-semibold rounded-xl transition-colors flex items-center justify-center gap-1.5 text-xs"
            >
              View Triage Feed
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
