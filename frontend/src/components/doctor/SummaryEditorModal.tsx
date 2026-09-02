import React, { useState } from 'react';
import {
  Check,
  X,
  FileCheck,
  Send,
  AlertTriangle,
  Sparkles,
  Save,
  CheckCircle,
} from 'lucide-react';
import { ClinicalSummary } from '../../types';
import { api } from '../../services/api';

interface SummaryEditorModalProps {
  summary: ClinicalSummary;
  onClose: () => void;
  onUpdate: (updated: ClinicalSummary) => void;
}

export const SummaryEditorModal: React.FC<SummaryEditorModalProps> = ({ summary, onClose, onUpdate }) => {
  const [formData, setFormData] = useState({
    chiefComplaintText: summary.chiefComplaintText || '',
    historyOfPresentIllness: summary.historyOfPresentIllness || '',
    pastMedicalHistory: summary.pastMedicalHistory || '',
    pastSurgicalHistory: summary.pastSurgicalHistory || '',
    drugAndAllergyHistory: summary.drugAndAllergyHistory || '',
    currentMedications: summary.currentMedications || '',
    potentialRedFlags: summary.potentialRedFlags || '',
    ayushAssessmentSummary: summary.ayushAssessmentSummary || '',
    physicianNotes: summary.physicianNotes || '',
    doctorName: 'Dr. Rajesh Sharma, MD',
  });
  const [isSaving, setIsSaving] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = async (action: 'SAVE_DRAFT' | 'CONFIRM') => {
    setIsSaving(true);
    try {
      const updated = await api.editSummary(summary.id, {
        ...formData,
        action,
      });
      onUpdate(updated);
      onClose();
    } catch (err) {
      console.error('Error updating summary:', err);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs overflow-y-auto animate-fade-in">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 max-w-4xl w-full max-h-[90vh] flex flex-col overflow-hidden animate-slide-up my-auto">
        {/* Header */}
        <div className="px-6 py-4 bg-slate-50 border-b border-slate-200 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-med-600 text-white flex items-center justify-center shadow-xs">
              <FileCheck className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base font-extrabold text-slate-900">
                  Physician Clinical Draft Review & Verification
                </h3>
                <span className="px-2 py-0.5 text-[10px] font-bold uppercase rounded bg-amber-100 text-amber-800 border border-amber-200">
                  Editable Draft
                </span>
              </div>
              <p className="text-xs text-slate-500">
                Review, edit, and confirm intake details for {summary.patientName || 'Patient'} ({summary.tokenNumber || '#104'})
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-200 rounded-lg transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Safety Disclaimer Banner */}
        <div className="bg-amber-50 px-6 py-2.5 border-b border-amber-200 flex items-center gap-2 text-xs font-semibold text-amber-900">
          <AlertTriangle className="w-4 h-4 text-amber-600 flex-shrink-0" />
          <span>
            <strong>Safety Requirement:</strong> AI outputs are drafts. The attending physician remains the sole decision maker before clinical orders.
          </span>
        </div>

        {/* Form Body */}
        <div className="p-6 overflow-y-auto space-y-5 flex-1 text-xs">
          {/* Chief Complaint */}
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
              Chief Complaint
            </label>
            <input
              type="text"
              name="chiefComplaintText"
              value={formData.chiefComplaintText}
              onChange={handleChange}
              className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
            />
          </div>

          {/* History of Present Illness (HPI) */}
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
              History of Present Illness (HPI)
            </label>
            <textarea
              name="historyOfPresentIllness"
              rows={4}
              value={formData.historyOfPresentIllness}
              onChange={handleChange}
              className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium leading-relaxed"
            />
          </div>

          {/* 2-Column Past Med & Surgical */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
                Past Medical History
              </label>
              <textarea
                name="pastMedicalHistory"
                rows={2}
                value={formData.pastMedicalHistory}
                onChange={handleChange}
                className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
              />
            </div>
            <div>
              <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
                Past Surgical History
              </label>
              <textarea
                name="pastSurgicalHistory"
                rows={2}
                value={formData.pastSurgicalHistory}
                onChange={handleChange}
                className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
              />
            </div>
          </div>

          {/* Active Meds & Allergies */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
                Active Current Medications
              </label>
              <textarea
                name="currentMedications"
                rows={2}
                value={formData.currentMedications}
                onChange={handleChange}
                className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
              />
            </div>
            <div>
              <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
                Drug & Allergy History
              </label>
              <textarea
                name="drugAndAllergyHistory"
                rows={2}
                value={formData.drugAndAllergyHistory}
                onChange={handleChange}
                className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
              />
            </div>
          </div>

          {/* Potential Red Flags */}
          <div>
            <label className="block font-bold text-red-800 uppercase tracking-wide mb-1">
              Potential Red Flags & Triage Alerts
            </label>
            <input
              type="text"
              name="potentialRedFlags"
              value={formData.potentialRedFlags}
              onChange={handleChange}
              className="w-full px-3 py-2 rounded-xl border border-red-300 bg-red-50/50 text-red-900 focus:ring-2 focus:ring-red-500 focus:border-transparent font-bold"
            />
          </div>

          {/* AYUSH Assessment */}
          <div>
            <label className="block font-bold text-ayush-800 uppercase tracking-wide mb-1">
              AYUSH Ayurvedic Assessment
            </label>
            <textarea
              name="ayushAssessmentSummary"
              rows={2}
              value={formData.ayushAssessmentSummary}
              onChange={handleChange}
              className="w-full px-3 py-2 rounded-xl border border-ayush-300 bg-ayush-50/40 text-ayush-950 focus:ring-2 focus:ring-ayush-500 focus:border-transparent font-medium"
            />
          </div>

          {/* Physician Notes */}
          <div>
            <label className="block font-bold text-slate-700 uppercase tracking-wide mb-1">
              Physician Consultation Notes & Final Plan
            </label>
            <textarea
              name="physicianNotes"
              rows={2}
              placeholder="Enter clinical examination notes, provisional diagnosis, and prescription plan..."
              value={formData.physicianNotes}
              onChange={handleChange}
              className="w-full px-3 py-2 rounded-xl border border-slate-300 focus:ring-2 focus:ring-med-500 focus:border-transparent font-medium"
            />
          </div>
        </div>

        {/* Footer Actions */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-slate-500 text-xs">
            <span>Signing Physician:</span>
            <strong className="text-slate-800">{formData.doctorName}</strong>
          </div>

          <div className="flex items-center gap-2.5">
            <button
              type="button"
              onClick={() => handleSave('SAVE_DRAFT')}
              disabled={isSaving}
              className="px-4 py-2 bg-white hover:bg-slate-100 text-slate-700 font-bold rounded-xl border border-slate-300 transition-colors flex items-center gap-1.5"
            >
              <Save className="w-3.5 h-3.5" />
              Save Draft Edits
            </button>
            <button
              type="button"
              onClick={() => handleSave('CONFIRM')}
              disabled={isSaving}
              className="px-5 py-2 bg-gradient-to-r from-med-600 to-teal-600 hover:from-med-700 hover:to-teal-700 text-white font-extrabold rounded-xl shadow-md transition-all flex items-center gap-1.5"
            >
              <CheckCircle className="w-4 h-4" />
              Verify & Confirm Summary
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
