package com.intellitransit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.intellitransit.service.QrTicketService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrTicketServiceImpl implements QrTicketService {

    private final ObjectMapper objectMapper;

    public QrTicketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateQrPayload(String ticketNumber, String bookingReference, Long passengerId, Long tripId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("ticketNumber", ticketNumber);
            payload.put("bookingReference", bookingReference);
            payload.put("passengerId", passengerId);
            payload.put("tripId", tripId);
            payload.put("timestamp", LocalDateTime.now().toString());

            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR payload JSON", e);
        }
    }

    @Override
    public byte[] generateQrCodeImagePng(String qrData, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code PNG image", e);
        }
    }

    @Override
    public String generateQrCodeBase64(String qrData, int width, int height) {
        byte[] imageBytes = generateQrCodeImagePng(qrData, width, height);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
