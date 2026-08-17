# IntelliTransit

> **Smart Transport Route, Fare and Passenger Service Management Platform with AI-Based Operational Anomaly Detection**

IntelliTransit is an enterprise-style public transport management platform engineered for urban transit operators. Built as a multi-tier solution with a JavaFX desktop frontend, Spring Boot backend, MySQL database, and Python FastAPI AI service, it provides operational monitoring, ticket booking, fare rule calculation, trip logs, and machine learning powered anomaly detection.

---

## Key Features & Architecture

### User Roles
- **Passenger**: Search routes, view schedules, calculate fares, book digital tickets, view ZXing QR codes, and file complaints.
- **Driver**: View assigned bus/route schedules, start/end operational trips, scan passenger QR tickets, and report incidents.
- **Operations Manager**: Modern enterprise dark dashboard for managing fleet (buses, drivers, routes, stops, schedules), reviewing operational logs, monitoring AI alerts, and viewing analytics.

### AI Operational Insights Pipeline
1. **Isolation Forest Anomaly Detection**: Unsupervised detection across Fare Anomalies, High Booking Demand Spikes, Trip Duration Deviations, and Complaint Volume Spikes.
2. **Rule-Based Recommendation Engine**: Maps detected anomalies to cautious operational action items.
3. **Google Gemini AI Explainer**: Generates natural language contextual explanations for detected operational anomalies.

---

## Repository Structure

```text
IntelliTransit/
├── backend/                  # Spring Boot 3.3 REST API (Java 21)
├── frontend-javafx/          # JavaFX 21 Enterprise Desktop GUI
├── ai-service/               # Python FastAPI Anomaly Detection & AI Explanation Service
├── docs/                     # Architecture, Database Schema, and Setup Guides
├── .gitignore                # Version control ignore definitions
└── README.md                 # Project Overview
```

---

## Quick Start Summary

1. **Database**: Run `docs/db_schema.sql` on MySQL server.
2. **Backend**: `cd backend && mvn spring-boot:run` (Runs on `http://localhost:8080`)
3. **AI Service**: `cd ai-service && uvicorn app.main:app --port 8000` (Runs on `http://localhost:8000`)
4. **Frontend**: `cd frontend-javafx && mvn javafx:run`

For detailed setup instructions, refer to [Developer Setup Guide](docs/setup_guide.md).
