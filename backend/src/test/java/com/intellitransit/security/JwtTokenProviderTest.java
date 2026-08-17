package com.intellitransit.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5971";
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", expirationMs);
    }

    @Test
    @DisplayName("Should generate valid JWT token and extract claims correctly")
    void testGenerateAndValidateToken() {
        String username = "testuser";
        Long userId = 101L;
        String role = "PASSENGER";

        String token = tokenProvider.generateTokenFromUsername(username, userId, role);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals(username, tokenProvider.getUsernameFromToken(token));
        assertEquals(userId, tokenProvider.getUserIdFromToken(token));
        assertEquals(role, tokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should return false when validating invalid JWT token")
    void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid.jwt.token.string"));
    }
}
