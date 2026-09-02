import React from 'react';
import { Check, User, MessageSquareText, FileText, ClipboardList, Stethoscope } from 'lucide-react';

interface ProgressStepperProps {
  currentStep: number; // 1: Identify, 2: Converse, 3: Scan, 4: Summarize, 5: Consult
  completeness?: number;
}

export const ProgressStepper: React.FC<ProgressStepperProps> = ({ currentStep, completeness = 0 }) => {
  const steps = [
    { number: 1, label: 'Identify', icon: User },
    { number: 2, label: 'Converse', icon: MessageSquareText },
    { number: 3, label: 'Scan Records', icon: FileText },
    { number: 4, label: 'Summarize', icon: ClipboardList },
    { number: 5, label: 'Consult', icon: Stethoscope },
  ];

  return (
    <div className="w-full bg-white border-b border-slate-200 py-3 px-4 shadow-xs">
      <div className="max-w-4xl mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-2 sm:space-x-4 w-full">
          {steps.map((step, idx) => {
            const isCompleted = step.number < currentStep;
            const isCurrent = step.number === currentStep;
            const Icon = step.icon;

            return (
              <React.Fragment key={step.number}>
                <div className="flex items-center gap-2">
                  <div
                    className={`w-8 h-8 sm:w-9 sm:h-9 rounded-full flex items-center justify-center font-bold text-xs transition-all duration-300 ${
                      isCompleted
                        ? 'bg-emerald-500 text-white shadow-xs'
                        : isCurrent
                        ? 'bg-med-600 text-white ring-4 ring-med-100 shadow-sm scale-105'
                        : 'bg-slate-100 text-slate-400 border border-slate-200'
                    }`}
                  >
                    {isCompleted ? <Check className="w-4 h-4 stroke-[3]" /> : <Icon className="w-4 h-4" />}
                  </div>
                  <span
                    className={`text-xs font-semibold hidden md:inline transition-colors ${
                      isCurrent
                        ? 'text-med-900 font-bold'
                        : isCompleted
                        ? 'text-emerald-700'
                        : 'text-slate-400'
                    }`}
                  >
                    {step.label}
                  </span>
                </div>

                {idx < steps.length - 1 && (
                  <div
                    className={`flex-1 h-1 rounded-full transition-all duration-500 ${
                      step.number < currentStep ? 'bg-emerald-400' : 'bg-slate-200'
                    }`}
                  />
                )}
              </React.Fragment>
            );
          })}
        </div>

        {completeness > 0 && (
          <div className="ml-4 pl-4 border-l border-slate-200 hidden sm:flex flex-col items-end">
            <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400">Intake Progress</span>
            <span className="text-sm font-extrabold text-med-700">{completeness}%</span>
          </div>
        )}
      </div>
    </div>
  );
};
