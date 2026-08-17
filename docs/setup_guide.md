# IntelliTransit — Developer & Setup Guide

## Prerequisites
- **JDK 21** or later
- **Maven 3.8+**
- **Python 3.10+**
- **MySQL Server 8.0+**
- **Gemini API Key** (for Google AI explanations)

---

## 1. Database Setup

1. Start your local MySQL service on port `3306`.
2. Execute the schema script:
   ```bash
   mysql -u root -p < docs/db_schema.sql
   ```
   Or allow Spring Boot to auto-create database tables on startup.

---

## 2. Spring Boot Backend Setup

1. Navigate to `backend/`:
   ```bash
   cd backend
   ```
2. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Compile & test:
   ```bash
   mvn clean compile
   ```
4. Run the backend server:
   ```bash
   mvn spring-boot:run
   ```
   The backend service starts at `http://localhost:8080`.

---

## 3. Python AI Service Setup

1. Navigate to `ai-service/`:
   ```bash
   cd ai-service
   ```
2. Create and activate a Python virtual environment:
   ```bash
   python -m venv venv
   # On Windows PowerShell:
   .\venv\Scripts\Activate.ps1
   # On Linux/macOS:
   source venv/bin/activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Set up environment variables in `.env`:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   BACKEND_URL=http://localhost:8080
   PORT=8000
   ```
5. Run the FastAPI server:
   ```bash
   uvicorn app.main:app --reload --port 8000
   ```
   API Docs available at `http://localhost:8000/docs`.

---

## 4. JavaFX Frontend Setup

1. Navigate to `frontend-javafx/`:
   ```bash
   cd frontend-javafx
   ```
2. Compile the JavaFX desktop application:
   ```bash
   mvn clean compile
   ```
3. Launch the desktop user interface:
   ```bash
   mvn javafx:run
   ```

---

## Port Allocation Summary

| Component | Default Port | Protocol |
| :--- | :--- | :--- |
| **Spring Boot Backend** | `8080` | HTTP / REST |
| **Python FastAPI AI** | `8000` | HTTP / REST |
| **MySQL Database** | `3306` | JDBC |
| **JavaFX Client** | N/A (Desktop App) | HTTP Client |
