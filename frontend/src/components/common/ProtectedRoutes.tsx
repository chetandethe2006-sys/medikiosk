import React from 'react';
import { Navigate } from 'react-router-dom';

export const PatientProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const role = localStorage.getItem('user_role');
  if (!role || (role !== 'ROLE_PATIENT' && role !== 'ROLE_DOCTOR')) {
    return <Navigate to="/patient/login" replace />;
  }
  return <>{children}</>;
};

export const DoctorProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const role = localStorage.getItem('user_role');
  if (role !== 'ROLE_DOCTOR') {
    return <Navigate to="/doctor/login" replace />;
  }
  return <>{children}</>;
};
