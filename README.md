# MediKiosk — AI-Powered Patient Case-Taking & Clinical Intake Platform

> **Smart India Hackathon 2026 Prototype**  
> **Problem Statement ID:** 26047  
> **Title:** Patient Case-Taking Software  
> **Organization:** Ministry of Ayush  
> **Department:** All India Institute of Ayurveda (AIIA)  
> **Project Name:** MediKiosk  
> **Scope:** Complete 30% Vertical Slice MVP Prototype  

---

## 1. Executive Summary & Problem Transformation

In high-volume Indian hospital outpatient departments (OPDs), physicians often spend up to **70–80% of their consultation time asking repetitive history questions** and sifting through unorganized paper records.

**MediKiosk** transforms this into a digital self-service intake experience:

| Traditional OPD Bottleneck | MediKiosk Digital Self-Service Flow |
|---|---|
| Patient waits in long queues | Patient initiates intake at entrance kiosk |
| Repetitive verbal questioning | Multilingual AI interview (Marathi / Hindi / English) via Voice & Touch |
| Lost or disorganized paper records | Digitize prescriptions & lab reports via OCR |
| Unidentified emergency risks | Real-time Rule-Based Red-Flag Triage Detection |
| Doctor manually writes case history | AI generates structured draft with **"Doctor in 30 Seconds" Quick View** |
| Delayed treatment | Physician reviews, verifies draft, and syncs with ABDM / Hospital HIS |

---

## 2. Complete End-to-End Vertical Slice

The prototype provides a connected end-to-end journey:

```
[PATIENT KIOSK]
1. Identify (ABHA ID / Demographics / Demo Fast-track)
   ↓
2. Language Selection (Marathi / Hindi / English with Audio Pronunciation)
   ↓
3. Transparent Clinical Consent (Plain language + Speech Synthesis TTS)
   ↓
4. AI Clinical History Interview (Voice Input + Touch Chips + Adaptive Branching)
   ↓
5. Potential Red-Flag Detection (Real-Time Safety Rule Engine Alert)
   ↓
6. Document Digitization (Simulated OCR + Parameter Extraction + Abnormal Highlighting)
   ↓
7. Longitudinal Medical Timeline (Chronological past records & investigations)
   ↓
8. Structured Summary Draft Review & Queue Token Handover (#104)
   ↓
[DOCTOR DASHBOARD]
9. OPD Queue Monitor & Priority Triage Desk
   ↓
10. "Doctor in 30 Seconds" High-Density Scannable Quick View
   ↓
11. Deep Clinical Workspace (Overview, Transcript, Documents, Timeline, AYUSH, Audit)
   ↓
12. Physician Verification, Confirmation, and Mock FHIR R4 / ABDM / HIS Sync
```

---

## 3. Standout Features

### A. "Doctor in 30 Seconds" Clinical Quick View
When an attending physician opens a queued patient, MediKiosk renders a top-level high-density summary card containing:
- **Chief Complaint & Exact Onset**
- **Associated Symptoms & Radiation**
- **Potential Red Flag Alert Level**
- **Past Medical & Surgical History**
- **Active Medications**
- **Recent Investigation Highlights (Hb, Blood Glucose, Creatinine)**

### B. Rule-Based Red-Flag Detection & Safety Engine
- Detects high-risk symptom clusters (e.g. *Chest pain + breathlessness + diaphoresis*).
- Immediately dispatches a priority ticket to the `/triage` dashboard.
- Adheres strictly to healthcare software safety guidelines (strictly labels all outputs as *"Potential Red Flag"* and *"AI Generated Draft — Physician Verification Required"*).

### C. AIIA AYUSH Integrative Assessment Mode
- Built-in clinical matrix for Ayurvedic case-taking:
  - **Prakriti** (Vata, Pitta, Kapha constitutional assessment)
  - **Agni** (Sama, Vishama, Tikshna, Manda digestive fire evaluation)
  - **Koshtha** (Bowel motility)
  - **Ahara & Vihara** (Dietary habits and daily lifestyle routine)
  - **Nidana & Samprapti** (Aetiological factors and disease pathogenesis)

### D. Multilingual Speech & Touch Dual-Input
- Full native support for **Marathi (मराठी)**, **Hindi (हिन्दी)**, and **English**.
- Dual input on every screen: **Microphone Speech-to-Text** (Web Speech API with graceful fallback) or **Large Touch-Friendly Quick Chips**.

### E. Mock ABDM & FHIR R4 Bundle Architecture
- Produces HL7 FHIR R4 Composition & DiagnosticReport bundles ready for National Health Authority (NHA) ABDM gateways (`IN-MH-AIIA-0021`).

---

## 4. Technology Stack

### Frontend
- **Framework:** React 18 + TypeScript + Vite
- **Styling:** Tailwind CSS + Custom Medical Design Tokens + Glassmorphism
- **Routing:** React Router v6
- **Icons:** Lucide React
- **API Client:** Axios
- **Speech:** Web Speech API + SpeechSynthesis TTS

### Backend
- **Framework:** Java 21 LTS + Spring Boot 3.3.4
- **ORM & Data:** Spring Data JPA + Hibernate
- **Validation:** Jakarta Bean Validation
- **Security:** Spring Security stateless CORS configuration
- **Build Tool:** Apache Maven 3.9.6

### Database
- **Development (Default):** H2 In-Memory embedded database (Zero external installation required)
- **Production Simulation:** PostgreSQL 16 (via `docker-compose.yml`)

---

## 5. How to Run Locally

### Prerequisites
- **Java:** JDK 21+
- **Node.js:** Node v18+ / npm

### Step 1: Start the Backend (Spring Boot)

Open a terminal in the root directory:

```bash
cd backend
mvn spring-boot:run
```

*Note: The backend automatically seeds 4 rich clinical demo scenarios (Sunita Patil, Rahul More, Asha Kulkarni, Vijay Shinde) into the database upon startup.*
- **Backend API:** `http://localhost:8080/api`
- **H2 Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:medikioskdb`, User: `sa`, Password: `password`)

### Step 2: Start the Frontend (React + Vite)

Open a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

- **Frontend Application:** `http://localhost:5173`

---

## 6. Preloaded Demo Patient Scenarios

| Patient Name | Age/Gender | Language | Clinical Complaint | Highlight Features |
|---|---|---|---|---|
| **Sunita Patil** *(Primary Demo)* | 52 F | Marathi / English | Chest Pain (24h) | Priority Red Flag, Lab OCR (Hb 10.2, Glucose 138), Timeline, AYUSH Mode |
| **Rahul More** | 28 M | Hindi / English | High Fever & Body Ache (3d) | Dengue/Pyrexia Triage Evaluation, Paracetamol SOS |
| **Asha Kulkarni** | 45 F | English / Marathi | Type-2 Diabetes & Fatigue | AYUSH Assessment (Prakriti: Kapha-Pitta, Agni: Manda) |
| **Vijay Shinde** | 63 M | English | Chronic Exertional Cough | Past Hospital Discharge Summary |

---

## 7. SIH Live Presentation Walkthrough Script

1. **Landing Page (`/`)**: Click **"Start Patient Intake"**.
2. **Language (`/patient/language`)**: Select **मराठी** (Hear native audio greeting).
3. **Consent (`/patient/consent`)**: Click **"Hear Explanation"** audio TTS -> Click **"I Understand & Consent"**.
4. **Identification (`/patient/identity`)**: Click **"Use Recommended Demo Patient (Sunita Patil)"** -> Toggle **AYUSH Mode**.
5. **Clinical Interview (`/patient/history`)**:
   - Speak or tap **"Started yesterday"** -> **"Center of chest radiating to left arm"** -> **"Heavy crushing pressure"** -> **"8 / 10"** -> **"Breathlessness & Sweating"**.
   - Watch the **Potential Red Flag Alert Modal** trigger in real time.
6. **Documents (`/patient/documents`)**:
   - Click **"+ Biochemical Blood Panel"** -> Watch animated OCR extraction -> See abnormal **Hb 10.2 (Low)** and **Glucose 138 (High)** parameters.
7. **Review & Summary (`/patient/review` & `/patient/summary`)**:
   - Review captured points -> Click **"Submit & Join Doctor Queue"** -> Receive Token **#104**.
8. **Doctor Dashboard (`/doctor`)**:
   - View OPD stats (24 queue, 3 red flags) -> Open Sunita Patil (#104).
   - Point out **"Doctor in 30 Seconds" Quick View**.
   - Navigate tabs: **Overview**, **Transcript**, **Documents**, **Timeline**, **AYUSH Matrix**, **Audit Trail**.
   - Click **"Verify & Confirm Summary"** -> Click **"Sync to HIS / ABDM"** -> See FHIR R4 Bundle confirmation.

---

## 8. Integration Architecture & Mock Disclaimer

> **Transparency Notice:** This prototype uses a clean adapter/interface architecture (`AIHistoryService`, `DocumentExtractionService`, `ABDMIntegrationService`). For live demonstrations, deterministic mock providers are active to guarantee 100% offline reliability without external API keys or government network credentials. Real Gemini LLM, Tesseract OCR, and ABDM Sandbox endpoints can be enabled via configuration.
