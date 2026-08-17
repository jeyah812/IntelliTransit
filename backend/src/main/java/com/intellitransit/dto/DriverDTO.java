package com.intellitransit.dto;

import com.intellitransit.entity.enums.DriverStatus;

public class DriverDTO {

    private Long id;
    private Long userId;
    private String employeeId;
    private String fullName;
    private String phone;
    private String licenseNumber;
    private DriverStatus status;

    public DriverDTO() {}

    public DriverDTO(Long id, Long userId, String employeeId, String fullName, String phone, String licenseNumber, DriverStatus status) {
        this.id = id;
        this.userId = userId;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.status = status;
    }

    public static DriverDTOBuilder builder() {
        return new DriverDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public static class DriverDTOBuilder {
        private Long id;
        private Long userId;
        private String employeeId;
        private String fullName;
        private String phone;
        private String licenseNumber;
        private DriverStatus status;

        public DriverDTOBuilder id(Long id) { this.id = id; return this; }
        public DriverDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public DriverDTOBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public DriverDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public DriverDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public DriverDTOBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }
        public DriverDTOBuilder status(DriverStatus status) { this.status = status; return this; }

        public DriverDTO build() {
            return new DriverDTO(id, userId, employeeId, fullName, phone, licenseNumber, status);
        }
    }
}
