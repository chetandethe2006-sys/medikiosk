import React from 'react';
import { Calendar, FileText, Pill, Activity, Building2, ExternalLink } from 'lucide-react';
import { MedicalTimelineEvent } from '../../types';

interface ClinicalTimelineViewProps {
  events: MedicalTimelineEvent[];
}

export const ClinicalTimelineView: React.FC<ClinicalTimelineViewProps> = ({ events }) => {
  if (!events || events.length === 0) {
    return (
      <div className="bg-white rounded-2xl p-8 border border-slate-200 text-center text-slate-500">
        <Calendar className="w-10 h-10 mx-auto text-slate-300 mb-2" />
        <p className="font-semibold">No previous medical records uploaded yet.</p>
      </div>
    );
  }

  const getCategoryIcon = (category: string) => {
    switch (category.toUpperCase()) {
      case 'PRESCRIPTION':
        return <Pill className="w-4 h-4 text-blue-600" />;
      case 'LAB_REPORT':
        return <Activity className="w-4 h-4 text-emerald-600" />;
      default:
        return <FileText className="w-4 h-4 text-purple-600" />;
    }
  };

  const getCategoryColor = (category: string) => {
    switch (category.toUpperCase()) {
      case 'PRESCRIPTION':
        return 'bg-blue-50 border-blue-200 text-blue-800';
      case 'LAB_REPORT':
        return 'bg-emerald-50 border-emerald-200 text-emerald-800';
      default:
        return 'bg-purple-50 border-purple-200 text-purple-800';
    }
  };

  return (
    <div className="bg-white rounded-2xl p-6 border border-slate-200 shadow-sm">
      <div className="flex items-center justify-between mb-6 pb-3 border-b border-slate-100">
        <div>
          <h3 className="text-base font-bold text-slate-900">Longitudinal Medical Timeline</h3>
          <p className="text-xs text-slate-500">Chronological history from uploaded records and past hospital visits</p>
        </div>
        <span className="text-xs font-semibold px-2.5 py-1 bg-slate-100 text-slate-700 rounded-lg">
          {events.length} Historical Records
        </span>
      </div>

      <div className="relative pl-6 space-y-8 before:absolute before:left-3 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-200">
        {events.map((event, idx) => (
          <div key={event.id || idx} className="relative group">
            {/* Timeline bullet dot */}
            <div className="absolute -left-6 top-1.5 w-6 h-6 rounded-full bg-white border-2 border-med-600 flex items-center justify-center shadow-xs group-hover:scale-110 transition-transform">
              <div className="w-2 h-2 rounded-full bg-med-600" />
            </div>

            {/* Event Card */}
            <div className="bg-slate-50 hover:bg-slate-100/80 border border-slate-200 rounded-xl p-4 transition-all">
              <div className="flex flex-wrap items-center justify-between gap-2 mb-2">
                <div className="flex items-center gap-2">
                  <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-md text-xs font-bold border ${getCategoryColor(event.category)}`}>
                    {getCategoryIcon(event.category)}
                    {event.category.replace('_', ' ')}
                  </span>
                  <h4 className="text-sm font-bold text-slate-900">{event.title}</h4>
                </div>

                <div className="flex items-center gap-1.5 text-xs font-medium text-slate-500">
                  <Calendar className="w-3.5 h-3.5" />
                  <span>{new Date(event.eventDate).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })}</span>
                </div>
              </div>

              {event.summary && (
                <p className="text-xs text-slate-700 font-medium mb-2 leading-relaxed">
                  <strong className="text-slate-900">Findings: </strong>
                  {event.summary}
                </p>
              )}

              {event.medications && (
                <div className="text-xs text-slate-600 bg-white/80 p-2 rounded-lg border border-slate-200 mb-2">
                  <span className="font-semibold text-slate-800">Medications: </span>
                  {event.medications}
                </div>
              )}

              <div className="flex items-center justify-between text-[11px] text-slate-400 pt-2 border-t border-slate-200/60">
                <span className="flex items-center gap-1">
                  <Building2 className="w-3 h-3 text-slate-400" />
                  {event.facilityOrDoctor || 'AIIA OPD'}
                </span>
                {event.documentRef && (
                  <span className="font-mono text-med-700 bg-med-50 px-2 py-0.5 rounded border border-med-200">
                    {event.documentRef}
                  </span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
