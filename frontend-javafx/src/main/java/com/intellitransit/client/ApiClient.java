package com.intellitransit.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static ApiClient instance;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public <T> T get(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        validateResponse(response);

        return objectMapper.readValue(response.body(), responseType);
    }

    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        addAuthHeader(builder);

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        validateResponse(response);

        return objectMapper.readValue(response.body(), responseType);
    }

    public byte[] fetchImageBytes(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .header("Accept", "image/png");

        addAuthHeader(builder);

        HttpResponse<byte[]> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() >= 400) {
            throw new IOException("HTTP Error " + response.statusCode() + " while fetching image");
        }

        return response.body();
    }

    private void addAuthHeader(HttpRequest.Builder builder) {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            builder.header("Authorization", "Bearer " + session.getToken());
        }
    }

    private void validateResponse(HttpResponse<String> response) throws IOException {
        if (response.statusCode() >= 400) {
            String errorMsg = "HTTP Error " + response.statusCode();
            try {
                var node = objectMapper.readTree(response.body());
                if (node.has("message")) {
                    errorMsg = node.get("message").asText();
                }
            } catch (Exception ignored) {}
            throw new IOException(errorMsg);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
