package com.intellitransit.client;

public class UserSession {
    private static UserSession instance;

    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role; // PASSENGER, DRIVER, OPERATIONS_MANAGER

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void initSession(String token, Long userId, String username, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public void clear() {
        this.token = null;
        this.userId = null;
        this.username = null;
        this.email = null;
        this.role = null;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
