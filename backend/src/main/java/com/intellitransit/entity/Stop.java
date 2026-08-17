package com.intellitransit.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Stop() {}

    public Stop(Long id, String name, BigDecimal latitude, BigDecimal longitude, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static StopBuilder builder() {
        return new StopBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class StopBuilder {
        private Long id;
        private String name;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private LocalDateTime createdAt;

        public StopBuilder id(Long id) { this.id = id; return this; }
        public StopBuilder name(String name) { this.name = name; return this; }
        public StopBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public StopBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }
        public StopBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Stop build() {
            return new Stop(id, name, latitude, longitude, createdAt);
        }
    }
}
