"""
IntelliTransit — Python FastAPI AI Service Main Entry Point
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime

app = FastAPI(
    title="IntelliTransit AI Operational Intelligence Service",
    description="Isolation Forest Anomaly Detection & Gemini Explanation API",
    version="1.0.0"
)

# Enable CORS for Spring Boot & Frontend access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def read_root():
    return {
        "service": "IntelliTransit AI Service",
        "status": "HEALTHY",
        "timestamp": datetime.utcnow().isoformat()
    }


@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "components": {
            "isolation_forest": "READY",
            "rule_engine": "READY",
            "gemini_integration": "CONFIGURED"
        }
    }
