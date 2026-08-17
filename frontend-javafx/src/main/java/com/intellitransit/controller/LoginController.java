package com.intellitransit.controller;

import com.intellitransit.client.ApiClient;
import com.intellitransit.client.UserSession;
import com.intellitransit.model.FrontendModels.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private ComboBox<String> loginRoleCombo;

    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regFullNameField;
    @FXML private TextField regPhoneField;
    @FXML private ComboBox<String> regRoleCombo;

    @FXML private Label statusLabel;
    @FXML private TabPane authTabPane;

    @FXML
    public void initialize() {
        loginRoleCombo.setItems(FXCollections.observableArrayList("PASSENGER", "DRIVER", "OPERATIONS_MANAGER"));
        loginRoleCombo.getSelectionModel().select("PASSENGER");

        regRoleCombo.setItems(FXCollections.observableArrayList("PASSENGER", "DRIVER", "OPERATIONS_MANAGER"));
        regRoleCombo.getSelectionModel().select("PASSENGER");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();
        String role = loginRoleCombo.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        setStatus("Authenticating...", false);

        new Thread(() -> {
            try {
                LoginRequestModel request = new LoginRequestModel(username, password);
                AuthResponseModel response = ApiClient.getInstance().post("/api/auth/login", request, AuthResponseModel.class);

                UserSession.getInstance().initSession(
                        response.getAccessToken(),
                        response.getUserId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getRole().toString()
                );

                Platform.runLater(this::navigateToDashboard);

            } catch (Exception e) {
                Platform.runLater(() -> showError("Login Failed: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();
        String fullName = regFullNameField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String role = regRoleCombo.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        setStatus("Registering new account...", false);

        new Thread(() -> {
            try {
                RegisterRequestModel request = new RegisterRequestModel(username, email, password, fullName, phone, role);
                AuthResponseModel response = ApiClient.getInstance().post("/api/auth/register", request, AuthResponseModel.class);

                UserSession.getInstance().initSession(
                        response.getAccessToken(),
                        response.getUserId(),
                        response.getUsername(),
                        response.getEmail(),
                        response.getRole().toString()
                );

                Platform.runLater(this::navigateToDashboard);

            } catch (Exception e) {
                Platform.runLater(() -> showError("Registration Failed: " + e.getMessage()));
            }
        }).start();
    }

    private void navigateToDashboard() {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/intellitransit/fxml/main_dashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 800);
            stage.setTitle("IntelliTransit — Enterprise Management Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Failed to load dashboard interface: " + e.getMessage());
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        if (isError) {
            statusLabel.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold;");
        }
    }

    private void showError(String message) {
        setStatus(message, true);
    }
}
