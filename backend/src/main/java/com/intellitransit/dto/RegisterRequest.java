package com.intellitransit.dto;

import com.intellitransit.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    @NotNull(message = "Role is required")
    private UserRole role;

    private String employeeId;
    private String licenseNumber;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String fullName, String phone, UserRole role, String employeeId, String licenseNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.employeeId = employeeId;
        this.licenseNumber = licenseNumber;
    }

    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public static class RegisterRequestBuilder {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private String phone;
        private UserRole role;
        private String employeeId;
        private String licenseNumber;

        public RegisterRequestBuilder username(String username) { this.username = username; return this; }
        public RegisterRequestBuilder email(String email) { this.email = email; return this; }
        public RegisterRequestBuilder password(String password) { this.password = password; return this; }
        public RegisterRequestBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public RegisterRequestBuilder phone(String phone) { this.phone = phone; return this; }
        public RegisterRequestBuilder role(UserRole role) { this.role = role; return this; }
        public RegisterRequestBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public RegisterRequestBuilder licenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; return this; }

        public RegisterRequest build() {
            return new RegisterRequest(username, email, password, fullName, phone, role, employeeId, licenseNumber);
        }
    }
}
