package com.intellitransit.service;

public interface QrTicketService {
    String generateQrPayload(String ticketNumber, String bookingReference, Long passengerId, Long tripId);
    byte[] generateQrCodeImagePng(String qrData, int width, int height);
    String generateQrCodeBase64(String qrData, int width, int height);
}
