import React from 'react';
import { Leaf, Flame, Utensils, Moon, Shield, Info } from 'lucide-react';
import { ClinicalHistory } from '../../types';

interface AyushAssessmentViewProps {
  history?: ClinicalHistory | null;
}

export const AyushAssessmentView: React.FC<AyushAssessmentViewProps> = ({ history }) => {
  if (!history) return null;

  return (
    <div className="bg-gradient-to-br from-ayush-50 to-white rounded-2xl p-6 border border-ayush-200 shadow-sm space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-4 border-b border-ayush-200">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-ayush-600 text-white flex items-center justify-center shadow-md shadow-ayush-600/20">
            <Leaf className="w-6 h-6" />
          </div>
          <div>
            <h3 className="text-base font-extrabold text-ayush-900">
              AYUSH Ayurvedic Clinical Assessment
            </h3>
            <p className="text-xs text-ayush-700 font-medium">
              Standardized AIIA Ayurvedic Case-Taking & Constitutional Matrix
            </p>
          </div>
        </div>
        <span className="px-3 py-1 bg-ayush-100 text-ayush-800 text-xs font-bold rounded-full border border-ayush-300">
          AIIA Integrative OPD
        </span>
      </div>

      {/* Grid of Key Ayurvedic Parameters */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Prakriti */}
        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-1.5 text-xs font-bold text-ayush-800 uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4 text-ayush-600" />
            <span>Prakriti (Constitution)</span>
          </div>
          <p className="text-sm font-extrabold text-slate-900">
            {history.prakriti || 'Pitta-Vata'}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">Sensitivity to heat and physical exertion</p>
        </div>

        {/* Agni */}
        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-1.5 text-xs font-bold text-ayush-800 uppercase tracking-wider mb-1">
            <Flame className="w-4 h-4 text-amber-600" />
            <span>Agni (Digestive Fire)</span>
          </div>
          <p className="text-sm font-extrabold text-slate-900">
            {history.agni || 'Vishama Agni'}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">Irregular digestion and variable appetite</p>
        </div>

        {/* Koshtha */}
        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-1.5 text-xs font-bold text-ayush-800 uppercase tracking-wider mb-1">
            <Info className="w-4 h-4 text-teal-600" />
            <span>Koshtha (Bowel Habit)</span>
          </div>
          <p className="text-sm font-extrabold text-slate-900">
            {history.koshtha || 'Madhyama Koshtha'}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">Regular bowel elimination, occasional bloating</p>
        </div>

        {/* Vikriti */}
        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-1.5 text-xs font-bold text-red-700 uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4 text-red-600" />
            <span>Vikriti (Dosha Imbalance)</span>
          </div>
          <p className="text-sm font-extrabold text-slate-900">
            {history.vikriti || 'Vata-Pitta Prakopa'}
          </p>
          <p className="text-[11px] text-slate-500 mt-1">Hridroga Poorvaroopa correlation</p>
        </div>
      </div>

      {/* Detailed Lifestyle & Nidana Sections */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-bold text-ayush-900 uppercase tracking-wider mb-2">
            <Utensils className="w-4 h-4 text-ayush-600" />
            <span>Ahara & Vihara (Diet & Lifestyle)</span>
          </div>
          <p className="text-xs text-slate-700 leading-relaxed">
            {history.aharaVihara || 'Vegetarian diet, irregular meal timings, late dinners, high mental stress with reduced sleep duration.'}
          </p>
        </div>

        <div className="bg-white p-4 rounded-xl border border-ayush-200 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-bold text-ayush-900 uppercase tracking-wider mb-2">
            <Moon className="w-4 h-4 text-ayush-600" />
            <span>Nidana & Samprapti (Aetiology & Pathogenesis)</span>
          </div>
          <p className="text-xs text-slate-700 leading-relaxed">
            {history.nidana || 'Atichintana (chronic psychological stress), Vyayama Abhava (sedentary occupation), Ruksha-Tikshna Ahara leading to Vata-Pitta Srotorodha.'}
          </p>
        </div>
      </div>
    </div>
  );
};
