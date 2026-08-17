package com.intellitransit.entity;

import com.intellitransit.entity.enums.DriverStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_id", nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Driver() {}

    public Driver(Long id, User user, String employeeId, String fullName, String phone, String licenseNumber, DriverStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.status = status != null ? status : DriverStatus.AVAILABLE;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DriverStatus.AVAILABLE;
        }
    }

    public static DriverBuilder builder() {
        return new DriverBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class DriverBuilder {
        private Long id;
        private User user;
        private String employeeId;
        private String fullName;
        private String phone;
        private String licenseNumber;
        private DriverStatus status = DriverStatus.AVAILABLE;
        private LocalDateTime createdAt;

        public DriverBuilder id(Long id) { this.id = id; return this; }
        public DriverBuilder user(User user) { this.user = user; return this; }
        public DriverBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public DriverBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public DriverBuilder phone(String phone) { this.phone = phone; return this; }
        public DriverBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DriverBuilder status(DriverStatus status) { this.status = status; return this; }
        public DriverBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Driver build() {
            return new Driver(id, user, employeeId, fullName, phone, licenseNumber, status, createdAt);
        }
    }
}
