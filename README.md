# 🩺 transplant-service | Pediatric Nephrology Platform

## Microservice — Post-Renal Transplant Follow-Up

**Tech Stack:**
- Angular 17
- Tailwind CSS
- Spring Boot 3 (Java 17)
- PostgreSQL 15
- PubChem API

---

## 📌 Overview

The **transplant-service** is a dedicated microservice within the Pediatric Nephrology Platform responsible for managing all post-renal transplant clinical data.

It centralizes:
- Graft information
- Surveillance protocols
- Complication tracking
- Renal biopsies
- Real-time drug enrichment via PubChem API

---

## 🧠 Module 8 — Suivi Post-Greffe Rénale

This service covers:
- Donor dossier
- Post-transplant surveillance protocols
- Rejection episodes
- Renal biopsies
- Immunosuppressant protocol enrichment
- GFR (DFG) tracking

---

## 🏗️ Architecture

| Layer       | Technology                  | Role |
|------------|---------------------------|------|
| Frontend   | Angular 17 + Tailwind CSS | UI & reactive binding |
| Backend    | Spring Boot 3             | REST API & business logic |
| Database   | PostgreSQL 15             | Data persistence |
| External   | PubChem API               | Drug enrichment |
| HTTP Client| Axios / Angular HttpClient| API calls |
| Build      | Maven / Angular CLI       | Build & packaging |

---

## 📁 Project Structure

```
transplant-service/
├── src/
│   ├── main/java/com/nephro/transplant/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   ├── dto/
│   │   └── external/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
├── frontend/src/app/modules/transplant/
│   ├── components/
│   │   ├── drug-card/
│   │   ├── timeline/
│   │   └── protocole/
│   ├── services/
│   └── models/
└── README.md
```

---

## 🚀 Features

### Core CRUD Modules

- Donor dossier management
- Post-transplant surveillance protocols
- Rejection episode tracking
- Renal biopsy records (Banff score)
- GFR tracking (Schwartz / CKD-EPI)

---

## 💊 Advanced Feature — PubChem Drug Enrichment

Each immunosuppressant drug is enriched automatically via PubChem API.

---

## 🔌 API Endpoints

Base URL: /api/v1/transplant

---

## ⚙️ Environment Variables

SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nephro_db
SPRING_DATASOURCE_USERNAME=nephro_user
SPRING_DATASOURCE_PASSWORD=********
JWT_SECRET=your-secret
JWT_EXPIRATION_MS=86400000
SERVER_PORT=8083
PUBCHEM_BASE_URL=https://pubchem.ncbi.nlm.nih.gov/rest/pug

---

## ▶️ Getting Started

### Backend

cd transplant-service
mvn spring-boot:run

### Frontend

cd frontend
npm install
ng serve

---

## 👥 Authors

Pediatric Nephrology Platform — Module 8
