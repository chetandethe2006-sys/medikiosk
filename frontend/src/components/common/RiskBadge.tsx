import React from 'react';
import { AlertCircle, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { RiskLevel } from '../../types';

interface RiskBadgeProps {
  level?: RiskLevel | string;
  size?: 'sm' | 'md' | 'lg';
}

export const RiskBadge: React.FC<RiskBadgeProps> = ({ level = 'NORMAL', size = 'md' }) => {
  const norm = level.toUpperCase();

  const isPriority = norm === 'PRIORITY' || norm === 'CRITICAL' || norm === 'HIGH';
  const isReview = norm === 'REVIEW' || norm === 'MODERATE';

  const sizeClasses = {
    sm: 'px-2 py-0.5 text-[11px] gap-1',
    md: 'px-2.5 py-1 text-xs gap-1.5',
    lg: 'px-3.5 py-1.5 text-sm gap-2 font-bold',
  };

  if (isPriority) {
    return (
      <span
        className={`inline-flex items-center rounded-full font-bold bg-red-100 text-red-800 border border-red-200 shadow-xs ${sizeClasses[size]}`}
      >
        <AlertCircle className="w-3.5 h-3.5 text-red-600 stroke-[2.5]" />
        Priority Red Flag
      </span>
    );
  }

  if (isReview) {
    return (
      <span
        className={`inline-flex items-center rounded-full font-bold bg-amber-100 text-amber-800 border border-amber-200 shadow-xs ${sizeClasses[size]}`}
      >
        <AlertTriangle className="w-3.5 h-3.5 text-amber-600 stroke-[2.5]" />
        Clinical Review
      </span>
    );
  }

  return (
    <span
      className={`inline-flex items-center rounded-full font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200 ${sizeClasses[size]}`}
    >
      <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
      Standard Triage
    </span>
  );
};
