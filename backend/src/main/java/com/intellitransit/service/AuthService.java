package com.intellitransit.service;

import com.intellitransit.dto.AuthResponse;
import com.intellitransit.dto.LoginRequest;
import com.intellitransit.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
