package com.intellitransit.controller;

import com.intellitransit.client.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainDashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label roleBadgeLabel;
    @FXML private VBox sidebarNav;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            Platform.runLater(this::handleLogout);
            return;
        }

        usernameLabel.setText("User: " + session.getUsername());
        roleBadgeLabel.setText(session.getRole());

        buildSidebarNavigation(session.getRole());
        loadInitialRoleView(session.getRole());
    }

    private void buildSidebarNavigation(String role) {
        sidebarNav.getChildren().clear();

        if ("PASSENGER".equalsIgnoreCase(role)) {
            Button navPassenger = createNavButton("Passenger Workspace", true);
            navPassenger.setOnAction(e -> loadView("/com/intellitransit/fxml/passenger_dashboard.fxml"));
            sidebarNav.getChildren().add(navPassenger);
        } else if ("DRIVER".equalsIgnoreCase(role)) {
            Button navDriver = createNavButton("Driver Operations", true);
            navDriver.setOnAction(e -> loadView("/com/intellitransit/fxml/driver_dashboard.fxml"));
            sidebarNav.getChildren().add(navDriver);
        } else if ("OPERATIONS_MANAGER".equalsIgnoreCase(role)) {
            Button navManager = createNavButton("Operations Management", true);
            navManager.setOnAction(e -> loadView("/com/intellitransit/fxml/manager_dashboard.fxml"));
            sidebarNav.getChildren().add(navManager);
        }
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        if (active) {
            btn.getStyleClass().add("nav-btn-active");
        }
        return btn;
    }

    private void loadInitialRoleView(String role) {
        if ("PASSENGER".equalsIgnoreCase(role)) {
            loadView("/com/intellitransit/fxml/passenger_dashboard.fxml");
        } else if ("DRIVER".equalsIgnoreCase(role)) {
            loadView("/com/intellitransit/fxml/driver_dashboard.fxml");
        } else if ("OPERATIONS_MANAGER".equalsIgnoreCase(role)) {
            loadView("/com/intellitransit/fxml/manager_dashboard.fxml");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        UserSession.getInstance().clear();
        try {
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/intellitransit/fxml/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1280, 800);
            stage.setTitle("IntelliTransit — Sign In");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
