import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ShieldAlert,
  AlertTriangle,
  CheckCircle2,
  Clock,
  ArrowRight,
  RefreshCw,
  User,
  Heart,
  Eye,
  Building2,
  Check,
} from 'lucide-react';
import { api } from '../services/api';
import { RedFlagEvent } from '../types';

export const TriageMonitorPage: React.FC = () => {
  const [alerts, setAlerts] = useState<RedFlagEvent[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadAlerts();
  }, []);

  const loadAlerts = async () => {
    setIsLoading(true);
    try {
      const data = await api.getAllRedFlags();
      setAlerts(data);
    } catch (e) {
      console.error('Failed to load triage alerts:', e);
      setAlerts([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAcknowledge = async (id: number) => {
    try {
      const updated = await api.acknowledgeRedFlag(id, 'Dr. Rajesh Sharma');
      setAlerts((prev) => prev.map((a) => (a.id === id ? updated : a)));
    } catch (e) {
      setAlerts((prev) =>
        prev.map((a) =>
          a.id === id
            ? { ...a, triageStatus: 'ACKNOWLEDGED', acknowledgedByDoctor: 'Dr. Rajesh Sharma' }
            : a
        )
      );
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      <main className="flex-1 max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full space-y-6">
        {/* Header */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-2xl bg-red-600 text-white flex items-center justify-center shadow-md shadow-red-600/20">
              <ShieldAlert className="w-7 h-7 stroke-[2.5]" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                  Triage & Red-Flag Priority Monitor
                </h1>
                <span className="w-2.5 h-2.5 rounded-full bg-red-500 animate-pulse" />
              </div>
              <p className="text-xs text-slate-500 font-medium mt-0.5">
                Real-time safety rule engine intercepting high-risk patient presentations across kiosk terminals
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={loadAlerts}
            className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold rounded-xl transition-colors flex items-center gap-1.5 self-start sm:self-auto"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Refresh Feed</span>
          </button>
        </div>

        {/* Alerts Feed */}
        <div className="space-y-4">
          {alerts.map((alert) => {
            const isPending = alert.triageStatus === 'AWAITING_REVIEW';

            return (
              <div
                key={alert.id}
                className={`bg-white rounded-3xl p-6 border-2 transition-all shadow-sm ${
                  isPending
                    ? 'border-red-400 ring-4 ring-red-50'
                    : 'border-slate-200 opacity-80'
                }`}
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-slate-100">
                  <div className="flex items-center gap-3">
                    <span className="px-3 py-1 bg-red-600 text-white text-xs font-black rounded-full uppercase tracking-wider">
                      {alert.severity} ALERT
                    </span>
                    <span className="text-base font-black text-med-800 font-mono">
                      Token {alert.tokenNumber || '#104'}
                    </span>
                    <span className="text-sm font-extrabold text-slate-900">
                      {alert.patientName || 'Sunita Patil'} ({alert.patientAge}y / {alert.patientGender})
                    </span>
                  </div>

                  <div className="flex items-center gap-1.5 text-xs text-slate-500 font-medium">
                    <Clock className="w-3.5 h-3.5 text-slate-400" />
                    <span>{new Date(alert.detectedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                    <span className="text-slate-300">•</span>
                    <span className={`font-bold ${isPending ? 'text-red-600' : 'text-emerald-700'}`}>
                      {alert.triageStatus.replace('_', ' ')}
                    </span>
                  </div>
                </div>

                <div className="py-4 space-y-3 text-xs">
                  <div>
                    <h4 className="font-extrabold text-red-900 uppercase tracking-wide">
                      {alert.title}
                    </h4>
                    <p className="text-slate-700 font-semibold mt-1">
                      <strong className="text-slate-900">Reported Symptoms: </strong>
                      {alert.symptomsReported}
                    </p>
                  </div>

                  <div className="bg-red-50/80 p-3.5 rounded-xl border border-red-200/80 text-red-950 font-medium leading-relaxed">
                    <strong className="text-red-900">Action Protocol: </strong>
                    {alert.clinicalRecommendation}
                  </div>

                  {alert.acknowledgedByDoctor && (
                    <p className="text-slate-500 text-[11px]">
                      Acknowledged by: <strong>{alert.acknowledgedByDoctor}</strong>
                    </p>
                  )}
                </div>

                <div className="pt-2 flex flex-wrap items-center justify-end gap-3">
                  {isPending && (
                    <button
                      type="button"
                      onClick={() => handleAcknowledge(alert.id)}
                      className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 font-bold rounded-xl text-xs transition-colors flex items-center gap-1.5"
                    >
                      <Check className="w-3.5 h-3.5" />
                      <span>Acknowledge Priority</span>
                    </button>
                  )}

                  <button
                    type="button"
                    onClick={() => navigate(`/doctor/patient/${alert.patientId}`)}
                    className="px-5 py-2 bg-red-600 hover:bg-red-700 text-white font-bold rounded-xl text-xs transition-all shadow-md shadow-red-600/20 flex items-center gap-1.5"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    <span>Open Patient Case</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </main>
    </div>
  );
};
