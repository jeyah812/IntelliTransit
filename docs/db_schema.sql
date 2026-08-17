-- ============================================================================
-- INTELLITRANSIT — DATABASE INITIALIZATION SCHEMA (PHASE 2A)
-- Smart Transport Route, Fare and Passenger Service Management Platform
-- ============================================================================

CREATE DATABASE IF NOT EXISTS intellitransit_db;
USE intellitransit_db;

-- 1. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('PASSENGER', 'DRIVER', 'OPERATIONS_MANAGER') NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 2. PASSENGERS TABLE (Extends User via 1:1)
CREATE TABLE IF NOT EXISTS passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. DRIVERS TABLE (Extends User via 1:1)
CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    license_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('AVAILABLE', 'ON_TRIP', 'OFF_DUTY') NOT NULL DEFAULT 'AVAILABLE',
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. BUSES TABLE
CREATE TABLE IF NOT EXISTS buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(30) NOT NULL UNIQUE,
    bus_number VARCHAR(30) NOT NULL,
    model VARCHAR(50),
    capacity INT NOT NULL,
    status ENUM('ACTIVE', 'MAINTENANCE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL
);

-- 5. ROUTES TABLE
CREATE TABLE IF NOT EXISTS routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_number VARCHAR(30) NOT NULL UNIQUE,
    route_name VARCHAR(100) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance_km DECIMAL(6, 2) NOT NULL,
    estimated_duration_minutes INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL
);

-- 6. STOPS TABLE
CREATE TABLE IF NOT EXISTS stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    created_at DATETIME NOT NULL
);

-- 7. ROUTE_STOPS TABLE (Explicit Junction Entity)
CREATE TABLE IF NOT EXISTS route_stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    stop_sequence INT NOT NULL,
    distance_from_origin_km DECIMAL(6, 2) NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    FOREIGN KEY (stop_id) REFERENCES stops(id) ON DELETE CASCADE,
    UNIQUE KEY uk_route_sequence (route_id, stop_sequence)
);

-- 8. TRIPS TABLE
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    bus_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    scheduled_start DATETIME NOT NULL,
    scheduled_end DATETIME NOT NULL,
    actual_start DATETIME NULL,
    actual_end DATETIME NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    created_at DATETIME NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(id),
    FOREIGN KEY (bus_id) REFERENCES buses(id),
    FOREIGN KEY (driver_id) REFERENCES drivers(id)
);

-- 9. BOOKINGS TABLE
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    booking_reference VARCHAR(30) NOT NULL UNIQUE,
    booking_time DATETIME NOT NULL,
    fare_amount DECIMAL(8, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'CONFIRMED',
    FOREIGN KEY (passenger_id) REFERENCES passengers(id),
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);

-- 10. TICKETS TABLE
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    ticket_number VARCHAR(40) NOT NULL UNIQUE,
    qr_data TEXT NOT NULL,
    issued_at DATETIME NOT NULL,
    verified_at DATETIME NULL,
    status ENUM('ACTIVE', 'USED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- 11. FARE_RULES TABLE
CREATE TABLE IF NOT EXISTS fare_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    minimum_distance_km DECIMAL(6, 2) NOT NULL,
    maximum_distance_km DECIMAL(6, 2) NOT NULL,
    base_fare DECIMAL(6, 2) NOT NULL,
    student_discount_percentage DECIMAL(5, 2),
    senior_discount_percentage DECIMAL(5, 2),
    effective_from DATE NOT NULL,
    effective_to DATE NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
);

-- 12. COMPLAINTS TABLE
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    trip_id BIGINT NULL,
    subject VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('FARE_DISCREPANCY', 'SERVICE_DELAY', 'BUS_CONDITION', 'DRIVER_BEHAVIOR', 'OTHER') NOT NULL,
    status ENUM('OPEN', 'IN_REVIEW', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL,
    resolved_at DATETIME NULL,
    FOREIGN KEY (passenger_id) REFERENCES passengers(id),
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);

-- 13. TRIP_LOGS TABLE
CREATE TABLE IF NOT EXISTS trip_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NOT NULL,
    actual_start DATETIME NULL,
    actual_end DATETIME NULL,
    duration_minutes INT NULL,
    recorded_at DATETIME NOT NULL,
    notes TEXT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);

-- 14. AI_ALERTS TABLE
CREATE TABLE IF NOT EXISTS ai_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id BIGINT NULL,
    alert_type ENUM('FARE_ANOMALY', 'DEMAND_ANOMALY', 'TRIP_DURATION_ANOMALY', 'COMPLAINT_SPIKE') NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    anomaly_score DECIMAL(5, 4) NOT NULL,
    detected_at DATETIME NOT NULL,
    explanation TEXT NULL,
    recommendation TEXT NULL,
    status ENUM('NEW', 'REVIEWED', 'RESOLVED') NOT NULL DEFAULT 'NEW',
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE SET NULL
);
