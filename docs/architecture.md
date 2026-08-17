# IntelliTransit — System Architecture & Design Specification

## System Overview
**IntelliTransit** is an enterprise-grade smart public transport management platform with AI-based operational anomaly detection. It is designed for city public transport operators to streamline fleet management, fare calculation, ticket issuance, trip logging, and operational intelligence.

---

## 3-Tier Distributed Architecture

```text
+-------------------------------------------------------------------------+
|                         JavaFX 21 Frontend                              |
|   (Dark Enterprise Dashboard, Role Views: Passenger, Driver, Manager)   |
+-------------------------------------------------------------------------+
                                    |
                                    | REST / HTTP JSON
                                    v
+-------------------------------------------------------------------------+
|                      Spring Boot 3.3 Backend                            |
|  - Spring Web REST APIs           - Spring Data JPA                     |
|  - Spring Security + JWT          - ZXing QR Code Generation            |
|  - Fare Calculation Engine        - Incident & Trip Management          |
+-------------------------------------------------------------------------+
           |                                             |
           v Database Queries                            v REST API Call
+----------------------+                       +--------------------------+
|    MySQL Database    |                       |   Python FastAPI AI      |
|  (14 Core Relational |                       |   Service                |
|       Tables)        |                       | - Isolation Forest       |
+----------------------+                       | - Rule Engine            |
                                               | - Gemini AI Explainer    |
                                               +--------------------------+
```

---

## Core Modules & Responsibilities

| Module | Core Responsibilities | Key Technologies |
| :--- | :--- | :--- |
| **Module 1: User & Access** | Authentication, JWT issuing, role-based access control (Passenger, Driver, Operations Manager). | Spring Security, JJWT, BCrypt |
| **Module 2: Passenger Services** | Route search, stop lookup, digital ticket booking, ticket QR viewing, complaint filing. | Spring Web, JPA, FXML |
| **Module 3: Fleet & Route Management** | Bus, driver, route, stop, route-stop sequence, and trip schedule management. | Spring Data JPA, MySQL |
| **Module 4: Ticket & Fare Management** | Distance/rule-based fare calculation, ticket creation, ZXing QR generation & verification, payment status tracking. | ZXing, Spring Service |
| **Module 5: Trip Operations** | Scheduled trip management, driver start/end trip execution, duration tracking, incident reporting, operational trip logs. | Spring Service, JPA |
| **Module 6: AI Operational Insights** | Detection of fare anomalies, demand spikes, duration deviations, complaint volume spikes using Isolation Forest + Gemini explanations. | FastAPI, scikit-learn, Gemini API |

---

## AI Detection & Explanation Pipeline

```text
Operational Data (Trips, Bookings, Fares, Complaints)
                        |
                        v
        Python FastAPI Data Preprocessing
                        |
                        v
            scikit-learn Isolation Forest
                        |
              Anomaly Detected (score < threshold)
                        |
                        v
             Rule-Based Recommendation Engine
                        |
                        v
         Google Gemini API (Generative Contextual Explanation)
                        |
                        v
      Stored in `ai_alerts` & Displayed on Manager Dashboard
```

---

## User Roles & Key Workflows

1. **Passenger**: Registers/logs in $\rightarrow$ Searches routes $\rightarrow$ Reviews calculated fare $\rightarrow$ Books ticket $\rightarrow$ Receives ZXing QR ticket $\rightarrow$ Submits feedback/complaint if needed.
2. **Driver**: Logs in $\rightarrow$ Views assigned bus & trip schedule $\rightarrow$ Starts trip $\rightarrow$ Scans passenger QR ticket $\rightarrow$ Ends trip $\rightarrow$ Logs operational incident (if any).
3. **Operations Manager**: Monitors enterprise dark dashboard $\rightarrow$ Manages buses, drivers, routes, stops, schedules $\rightarrow$ Reviews automated AI anomaly alerts & recommendations $\rightarrow$ Generates operational reports.
