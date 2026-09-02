import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Stethoscope,
  Users,
  AlertTriangle,
  Clock,
  CheckCircle2,
  Search,
  Filter,
  ArrowRight,
  Eye,
  FileText,
  Building2,
  Sparkles,
  ShieldAlert,
} from 'lucide-react';
import { api } from '../services/api';
import { DoctorQueueItem, DoctorStats } from '../types';
import { RiskBadge } from '../components/common/RiskBadge';
import { StatusBadge } from '../components/common/StatusBadge';

export const DoctorDashboardPage: React.FC = () => {
  const [queue, setQueue] = useState<DoctorQueueItem[]>([]);
  const [stats, setStats] = useState<DoctorStats | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRisk, setFilterRisk] = useState<string>('ALL');
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [qData, sData] = await Promise.all([
        api.getDoctorQueue(),
        api.getDoctorStats(),
      ]);
      setQueue(qData);
      setStats(sData);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
      setQueue([]);
      setStats({
        totalQueueToday: 0,
        readyForConsultation: 0,
        redFlagsCount: 0,
        avgIntakeCompletionTime: '0m',
        completedToday: 0,
        doctorName: 'Doctor',
        opdRoom: 'OPD Room',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const filteredQueue = queue.filter((item) => {
    const matchesSearch =
      item.patientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.tokenNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.chiefComplaint.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesFilter =
      filterRisk === 'ALL' || item.riskLevel.toUpperCase() === filterRisk;

    return matchesSearch && matchesFilter;
  });

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col">
      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full space-y-8">
        {/* Top Doctor Welcome Bar & Metrics */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-3.5">
              <div className="w-12 h-12 rounded-2xl bg-med-600 text-white flex items-center justify-center shadow-md shadow-med-600/20">
                <Stethoscope className="w-7 h-7 stroke-[2.5]" />
              </div>
              <div>
                <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                  Good Morning, {stats?.doctorName || 'Dr. Rajesh Sharma'}
                </h1>
                <p className="text-xs text-slate-500 font-medium flex items-center gap-1.5 mt-0.5">
                  <Building2 className="w-3.5 h-3.5 text-slate-400" />
                  {stats?.opdRoom || 'OPD Room 104 — AIIA Department of Kayachikitsa & Medicine'}
                </p>
              </div>
            </div>

            <Link
              to="/triage"
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-red-50 hover:bg-red-100 text-red-800 border border-red-200 text-xs font-bold transition-colors self-start sm:self-auto"
            >
              <ShieldAlert className="w-4 h-4 text-red-600" />
              <span>{stats?.redFlagsCount || 3} Active Priority Red Flags</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          {/* 4 Metrics Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200/80">
              <div className="flex items-center gap-2 text-slate-500 text-xs font-bold uppercase tracking-wider mb-1">
                <Users className="w-4 h-4 text-blue-600" />
                <span>Today's Total Queue</span>
              </div>
              <p className="text-2xl font-black text-slate-900">{stats?.totalQueueToday || 24} <span className="text-xs text-slate-400 font-normal">Patients</span></p>
            </div>

            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200/80">
              <div className="flex items-center gap-2 text-slate-500 text-xs font-bold uppercase tracking-wider mb-1">
                <Clock className="w-4 h-4 text-med-600" />
                <span>Avg Intake Time</span>
              </div>
              <p className="text-2xl font-black text-med-700">{stats?.avgIntakeCompletionTime || '3m 42s'}</p>
            </div>

            <div className="bg-red-50/70 rounded-2xl p-4 border border-red-200">
              <div className="flex items-center gap-2 text-red-800 text-xs font-bold uppercase tracking-wider mb-1">
                <AlertTriangle className="w-4 h-4 text-red-600" />
                <span>Red Flags Detected</span>
              </div>
              <p className="text-2xl font-black text-red-700">{stats?.redFlagsCount || 3} <span className="text-xs text-red-500 font-normal">Priority</span></p>
            </div>

            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200/80">
              <div className="flex items-center gap-2 text-slate-500 text-xs font-bold uppercase tracking-wider mb-1">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                <span>Completed Today</span>
              </div>
              <p className="text-2xl font-black text-emerald-700">{stats?.completedToday || 18} <span className="text-xs text-slate-400 font-normal">Cases</span></p>
            </div>
          </div>
        </div>

        {/* Patient Queue Management Table */}
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden space-y-4 p-6">
          {/* Filters & Search */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-lg font-black text-slate-900 tracking-tight">Active OPD Patient Queue</h2>
              <p className="text-xs text-slate-500">Live intake statuses prepared by MediKiosk self-service stations</p>
            </div>

            <div className="flex flex-wrap items-center gap-3">
              {/* Search */}
              <div className="relative">
                <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                <input
                  type="text"
                  placeholder="Search token, name, complaint..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-9 pr-4 py-2 text-xs rounded-xl border border-slate-300 font-medium focus:ring-2 focus:ring-med-500 bg-slate-50"
                />
              </div>

              {/* Risk Filter */}
              <div className="flex items-center bg-slate-100 p-1 rounded-xl border border-slate-200 text-xs font-bold">
                {['ALL', 'PRIORITY', 'REVIEW', 'NORMAL'].map((r) => (
                  <button
                    key={r}
                    onClick={() => setFilterRisk(r)}
                    className={`px-3 py-1.5 rounded-lg transition-all ${
                      filterRisk === r ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'
                    }`}
                  >
                    {r === 'ALL' ? 'All Risks' : r}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-bold uppercase tracking-wider border-y border-slate-200">
                <tr>
                  <th className="py-3.5 px-4">Token</th>
                  <th className="py-3.5 px-4">Patient Details</th>
                  <th className="py-3.5 px-4">Chief Complaint</th>
                  <th className="py-3.5 px-4">Intake Status</th>
                  <th className="py-3.5 px-4">Triage Risk</th>
                  <th className="py-3.5 px-4">Records</th>
                  <th className="py-3.5 px-4 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredQueue.map((item) => (
                  <tr
                    key={item.sessionId}
                    onClick={() => navigate(`/doctor/patient/${item.patientId}`)}
                    className="hover:bg-med-50/40 cursor-pointer transition-colors group"
                  >
                    <td className="py-4 px-4 font-black text-sm text-med-800">
                      {item.tokenNumber}
                    </td>

                    <td className="py-4 px-4">
                      <div className="font-extrabold text-slate-900 text-sm group-hover:text-med-700">
                        {item.patientName}
                      </div>
                      <div className="text-slate-500 text-[11px]">
                        {item.age} yrs • {item.gender} {item.ayushMode && '• AYUSH Mode'}
                      </div>
                    </td>

                    <td className="py-4 px-4 font-medium text-slate-800 max-w-xs truncate">
                      {item.chiefComplaint}
                    </td>

                    <td className="py-4 px-4">
                      <StatusBadge status={item.intakeStatus} />
                    </td>

                    <td className="py-4 px-4">
                      <RiskBadge level={item.riskLevel} size="sm" />
                    </td>

                    <td className="py-4 px-4 text-slate-500 font-medium">
                      {item.documentCount > 0 ? `${item.documentCount} records (OCR)` : 'None'}
                    </td>

                    <td className="py-4 px-4 text-right">
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/doctor/patient/${item.patientId}`);
                        }}
                        className="px-3 py-1.5 bg-med-600 hover:bg-med-700 text-white font-bold rounded-lg text-xs transition-all shadow-xs flex items-center gap-1 ml-auto"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>Open Case</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  );
};
