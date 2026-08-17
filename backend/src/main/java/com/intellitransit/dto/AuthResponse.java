package com.intellitransit.dto;

import com.intellitransit.entity.enums.UserRole;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String tokenType, long expiresInMs, Long userId, String username, String email, String fullName, UserRole role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresInMs = expiresInMs;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresInMs;
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private UserRole role;

        public AuthResponseBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseBuilder expiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; return this; }
        public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseBuilder username(String username) { this.username = username; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder role(UserRole role) { this.role = role; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, tokenType, expiresInMs, userId, username, email, fullName, role);
        }
    }
}
