import React from 'react';
import { ShieldCheck, UserCheck, Cpu, Clock, CheckCircle } from 'lucide-react';
import { AuditEvent } from '../../types';

interface AuditTrailViewProps {
  logs: AuditEvent[];
}

export const AuditTrailView: React.FC<AuditTrailViewProps> = ({ logs }) => {
  if (!logs || logs.length === 0) {
    return (
      <div className="bg-white rounded-2xl p-8 border border-slate-200 text-center text-slate-500">
        <ShieldCheck className="w-10 h-10 mx-auto text-slate-300 mb-2" />
        <p className="font-semibold">No audit logs recorded for this patient yet.</p>
      </div>
    );
  }

  const getActorBadge = (actor: string) => {
    if (actor.includes('AI') || actor.includes('SYSTEM')) {
      return (
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-indigo-50 text-indigo-700 border border-indigo-200">
          <Cpu className="w-3 h-3" /> {actor}
        </span>
      );
    }
    if (actor.includes('DR_') || actor.includes('Dr.')) {
      return (
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
          <UserCheck className="w-3 h-3" /> Physician
        </span>
      );
    }
    return (
      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-bold bg-slate-100 text-slate-700 border border-slate-200">
        <UserCheck className="w-3 h-3" /> Patient Kiosk
      </span>
    );
  };

  return (
    <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm space-y-4">
      <div className="flex items-center justify-between pb-3 border-b border-slate-100">
        <div>
          <h3 className="text-base font-bold text-slate-900">Clinical Governance & Audit Trail</h3>
          <p className="text-xs text-slate-500">Immutable audit log for patient consent, AI extractions, and physician verification</p>
        </div>
        <span className="text-xs font-semibold px-2.5 py-1 bg-slate-100 text-slate-700 rounded-lg">
          {logs.length} Logged Events
        </span>
      </div>

      <div className="divide-y divide-slate-100">
        {logs.map((log) => (
          <div key={log.id} className="py-3 flex flex-col sm:flex-row sm:items-center justify-between gap-2 hover:bg-slate-50/80 px-2 rounded-lg transition-colors">
            <div className="flex items-start gap-3">
              <div className="w-2 h-2 rounded-full bg-med-500 mt-2 flex-shrink-0" />
              <div>
                <div className="flex items-center gap-2">
                  <span className="font-bold text-xs text-slate-900">{log.eventType}</span>
                  {getActorBadge(log.performedBy)}
                </div>
                <p className="text-xs text-slate-600 mt-0.5 leading-normal">{log.description}</p>
              </div>
            </div>

            <div className="flex items-center gap-1.5 text-[11px] text-slate-400 font-mono self-end sm:self-center">
              <Clock className="w-3 h-3" />
              <span>{new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
