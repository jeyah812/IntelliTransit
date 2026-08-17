# IntelliTransit — Python AI Service

AI Microservice component of the IntelliTransit platform responsible for operational anomaly detection and generative explanations.

## Architecture & Pipeline
1. **Isolation Forest Model (`scikit-learn`)**: Analyzes operational datasets (trip durations, booking demand counts, transaction fare differences, complaint frequencies) to flag statistical anomalies.
2. **Rule-Based Recommendation Engine**: Generates domain-specific operational action advice based on anomaly category.
3. **Google Gemini API Integration**: Produces clear natural language explanations for detected anomalies.

## Setup Instructions
```bash
python -m venv venv
# PowerShell:
.\venv\Scripts\Activate.ps1
# Linux/Mac:
source venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```
API Documentation will be available at `http://localhost:8000/docs`.
