package com.intellitransit.entity;

import com.intellitransit.entity.enums.BusStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registration_number", nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @Column(name = "bus_number", nullable = false, length = 30)
    private String busNumber;

    @Column(length = 50)
    private String model;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatus status = BusStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Bus() {}

    public Bus(Long id, String registrationNumber, String busNumber, String model, Integer capacity, BusStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.busNumber = busNumber;
        this.model = model;
        this.capacity = capacity;
        this.status = status != null ? status : BusStatus.ACTIVE;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static BusBuilder builder() {
        return new BusBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BusStatus getStatus() { return status; }
    public void setStatus(BusStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class BusBuilder {
        private Long id;
        private String registrationNumber;
        private String busNumber;
        private String model;
        private Integer capacity;
        private BusStatus status = BusStatus.ACTIVE;
        private LocalDateTime createdAt;

        public BusBuilder id(Long id) { this.id = id; return this; }
        public BusBuilder registrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; return this; }
        public BusBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public BusBuilder model(String model) { this.model = model; return this; }
        public BusBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public BusBuilder status(BusStatus status) { this.status = status; return this; }
        public BusBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Bus build() {
            return new Bus(id, registrationNumber, busNumber, model, capacity, status, createdAt);
        }
    }
}
