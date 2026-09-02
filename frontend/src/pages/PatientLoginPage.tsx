import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import { HeartPulse, LogIn, Lock, Mail, AlertCircle } from 'lucide-react';
import { GoogleLogin, CredentialResponse } from '@react-oauth/google';

export const PatientLoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');
    try {
      const res = await api.login(email, password);
      if (res.role === 'ROLE_PATIENT') {
        navigate('/patient/language');
      } else {
        setError('Unauthorized role for this portal.');
        api.logout();
      }
    } catch (err) {
      setError('Invalid email or password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleSuccess = async (credentialResponse: CredentialResponse) => {
    if (!credentialResponse.credential) {
      setError('Google sign-in failed. No credential received.');
      return;
    }
    setGoogleLoading(true);
    setError('');
    try {
      const res = await api.googleLogin(credentialResponse.credential, 'PATIENT');
      if (res.role === 'ROLE_PATIENT') {
        navigate('/patient/language');
      } else {
        setError('Unauthorized role for this portal.');
        api.logout();
      }
    } catch (err: any) {
      if (err.response && err.response.data) {
        setError(err.response.data);
      } else {
        setError('Google sign-in failed. Please try again.');
      }
    } finally {
      setGoogleLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-3xl p-8 border border-slate-200 shadow-xl">
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-2xl bg-med-100 text-med-600 flex items-center justify-center mx-auto mb-4">
            <HeartPulse className="w-8 h-8" />
          </div>
          <h1 className="text-2xl font-black text-slate-900">Patient Portal</h1>
          <p className="text-sm text-slate-500 mt-2">Sign in to start your clinical intake</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-700 p-3 rounded-lg text-sm font-medium mb-6 flex items-start gap-2 border border-red-200">
            <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Email Address</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3.5" />
              <input
                type="email"
                required
                disabled={isLoading || googleLoading}
                className="w-full pl-10 pr-4 py-3 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-med-500 disabled:opacity-50"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email"
              />
            </div>
          </div>
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3.5" />
              <input
                type="password"
                required
                disabled={isLoading || googleLoading}
                className="w-full pl-10 pr-4 py-3 rounded-xl border border-slate-300 text-sm focus:ring-2 focus:ring-med-500 disabled:opacity-50"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading || googleLoading}
            className="w-full py-3.5 bg-med-600 hover:bg-med-700 disabled:bg-med-400 text-white font-bold rounded-xl flex items-center justify-center gap-2 transition-colors mt-6"
          >
            <LogIn className="w-5 h-5" />
            {isLoading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="mt-6 flex items-center justify-between">
          <span className="border-b border-slate-200 w-full" />
          <span className="text-xs text-slate-400 px-3 uppercase font-bold">Or</span>
          <span className="border-b border-slate-200 w-full" />
        </div>

        <div className="mt-6 flex justify-center w-full">
          {googleLoading ? (
            <div className="w-full py-3.5 bg-slate-50 text-slate-500 font-bold rounded-xl flex items-center justify-center border border-slate-200">
              Signing in with Google...
            </div>
          ) : (
            <div className="w-full flex justify-center overflow-hidden rounded-xl">
              <GoogleLogin
                onSuccess={handleGoogleSuccess}
                onError={() => setError('Google sign-in was cancelled or failed.')}
                useOneTap={false}
                size="large"
                theme="outline"
                text="continue_with"
                shape="rectangular"
                width="100%"
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
