package com.intellitransit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellitransit.service.impl.QrTicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrTicketServiceTest {

    private QrTicketService qrTicketService;

    @BeforeEach
    void setUp() {
        qrTicketService = new QrTicketServiceImpl(new ObjectMapper());
    }

    @Test
    @DisplayName("Should generate valid JSON QR payload with expected fields")
    void testGenerateQrPayload() {
        String payload = qrTicketService.generateQrPayload("TK-12345", "BK-67890", 1L, 100L);

        assertNotNull(payload);
        assertTrue(payload.contains("TK-12345"));
        assertTrue(payload.contains("BK-67890"));
        assertTrue(payload.contains("\"passengerId\":1"));
        assertTrue(payload.contains("\"tripId\":100"));
    }

    @Test
    @DisplayName("Should generate valid PNG image byte array")
    void testGenerateQrCodeImagePng() {
        byte[] pngBytes = qrTicketService.generateQrCodeImagePng("SAMPLE_QR_DATA", 200, 200);

        assertNotNull(pngBytes);
        assertTrue(pngBytes.length > 0);
    }

    @Test
    @DisplayName("Should generate Base64 data URL string")
    void testGenerateQrCodeBase64() {
        String base64Data = qrTicketService.generateQrCodeBase64("SAMPLE_QR_DATA", 200, 200);

        assertNotNull(base64Data);
        assertTrue(base64Data.startsWith("data:image/png;base64,"));
    }
}
