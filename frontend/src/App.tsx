import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { LanguageProvider } from './context/LanguageContext';
import { IntakeProvider } from './context/IntakeContext';
import { AppHeader } from './components/common/AppHeader';

import { LandingPage } from './pages/LandingPage';
import { LanguagePage } from './pages/LanguagePage';
import { ConsentPage } from './pages/ConsentPage';
import { PatientIdentityPage } from './pages/PatientIdentityPage';
import { ClinicalHistoryPage } from './pages/ClinicalHistoryPage';
import { DocumentUploadPage } from './pages/DocumentUploadPage';
import { ReviewPage } from './pages/ReviewPage';
import { PatientSummaryPage } from './pages/PatientSummaryPage';
import { DoctorDashboardPage } from './pages/DoctorDashboardPage';
import { DoctorPatientViewPage } from './pages/DoctorPatientViewPage';
import { TriageMonitorPage } from './pages/TriageMonitorPage';
import { SettingsPage } from './pages/SettingsPage';
import { PatientLoginPage } from './pages/PatientLoginPage';
import { DoctorLoginPage } from './pages/DoctorLoginPage';
import { PatientProtectedRoute, DoctorProtectedRoute } from './components/common/ProtectedRoutes';

export function App() {
  return (
    <LanguageProvider>
      <IntakeProvider>
        <Router>
          <div className="min-h-screen flex flex-col font-sans">
            <AppHeader />
            <div className="flex-1">
              <Routes>
                <Route path="/" element={<LandingPage />} />
                <Route path="/patient/login" element={<PatientLoginPage />} />
                <Route path="/doctor/login" element={<DoctorLoginPage />} />
                
                {/* Patient Routes */}
                <Route path="/patient" element={<Navigate to="/patient/language" replace />} />
                <Route path="/patient/language" element={<PatientProtectedRoute><LanguagePage /></PatientProtectedRoute>} />
                <Route path="/patient/consent" element={<PatientProtectedRoute><ConsentPage /></PatientProtectedRoute>} />
                <Route path="/patient/identity" element={<PatientProtectedRoute><PatientIdentityPage /></PatientProtectedRoute>} />
                <Route path="/patient/history" element={<PatientProtectedRoute><ClinicalHistoryPage /></PatientProtectedRoute>} />
                <Route path="/patient/documents" element={<PatientProtectedRoute><DocumentUploadPage /></PatientProtectedRoute>} />
                <Route path="/patient/review" element={<PatientProtectedRoute><ReviewPage /></PatientProtectedRoute>} />
                <Route path="/patient/summary" element={<PatientProtectedRoute><PatientSummaryPage /></PatientProtectedRoute>} />
                <Route path="/settings" element={<PatientProtectedRoute><SettingsPage /></PatientProtectedRoute>} />
                
                {/* Doctor Routes */}
                <Route path="/doctor" element={<DoctorProtectedRoute><DoctorDashboardPage /></DoctorProtectedRoute>} />
                <Route path="/doctor/patient/:id" element={<DoctorProtectedRoute><DoctorPatientViewPage /></DoctorProtectedRoute>} />
                <Route path="/triage" element={<DoctorProtectedRoute><TriageMonitorPage /></DoctorProtectedRoute>} />
                
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </div>
          </div>
        </Router>
      </IntakeProvider>
    </LanguageProvider>
  );
}

export default App;
