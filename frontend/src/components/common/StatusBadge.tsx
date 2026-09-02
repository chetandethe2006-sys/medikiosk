import React from 'react';
import { IntakeStatus } from '../../types';

interface StatusBadgeProps {
  status?: IntakeStatus | string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status = 'IN_PROGRESS' }) => {
  const norm = status.toUpperCase();

  switch (norm) {
    case 'READY_FOR_DOCTOR':
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-blue-100 text-blue-800 border border-blue-200">
          Ready for Review
        </span>
      );
    case 'COMPLETED':
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800 border border-emerald-200">
          Consultation Done
        </span>
      );
    case 'IN_CONSULTATION':
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-purple-100 text-purple-800 border border-purple-200">
          In Consultation
        </span>
      );
    default:
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200">
          Intake in Progress
        </span>
      );
  }
};
