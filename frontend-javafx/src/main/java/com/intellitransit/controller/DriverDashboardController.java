package com.intellitransit.controller;

import com.intellitransit.client.ApiClient;
import com.intellitransit.model.FrontendModels.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DriverDashboardController {

    @FXML private TableView<TripModel> tripsTable;
    @FXML private TableColumn<TripModel, String> colTripId;
    @FXML private TableColumn<TripModel, String> colRoute;
    @FXML private TableColumn<TripModel, String> colBus;
    @FXML private TableColumn<TripModel, String> colStart;
    @FXML private TableColumn<TripModel, String> colEnd;
    @FXML private TableColumn<TripModel, String> colStatus;

    @FXML private Label selectedTripLabel;
    @FXML private Label tripActionStatusLabel;

    @FXML private TextField qrInputArea;
    @FXML private Label qrVerifyResultLabel;

    @FXML private TextArea incidentNotesArea;

    private final ObservableList<TripModel> assignedTripsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAssignedTrips();

        tripsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTripLabel.setText("Trip #" + newVal.getId() + " - " + newVal.getRouteNumber() + " (" + newVal.getStatus() + ")");
            }
        });
    }

    private void setupTableColumns() {
        colTripId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colRoute.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRouteNumber() + " - " + data.getValue().getRouteName()));
        colBus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBusNumber()));
        colStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getScheduledStart() != null ? data.getValue().getScheduledStart().toString() : ""));
        colEnd.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getScheduledEnd() != null ? data.getValue().getScheduledEnd().toString() : ""));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        tripsTable.setItems(assignedTripsList);
    }

    private void loadAssignedTrips() {
        new Thread(() -> {
            try {
                TripModel[] trips = ApiClient.getInstance().get("/api/driver/trips/assigned", TripModel[].class);
                Platform.runLater(() -> {
                    assignedTripsList.setAll(trips);
                    if (!assignedTripsList.isEmpty()) {
                        tripsTable.getSelectionModel().select(0);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleStartTrip(ActionEvent event) {
        TripModel selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setTripActionStatus("Please select a trip from the table first.", true);
            return;
        }

        setTripActionStatus("Starting trip #" + selected.getId() + "...", false);

        new Thread(() -> {
            try {
                TripModel updated = ApiClient.getInstance().post("/api/driver/trips/" + selected.getId() + "/start", null, TripModel.class);
                Platform.runLater(() -> {
                    setTripActionStatus("Trip #" + updated.getId() + " started! Driver Status: ON_TRIP", false);
                    loadAssignedTrips();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setTripActionStatus("Error Starting Trip: " + e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    public void handleEndTrip(ActionEvent event) {
        TripModel selected = tripsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setTripActionStatus("Please select a trip from the table first.", true);
            return;
        }

        String notes = incidentNotesArea.getText().trim();
        setTripActionStatus("Completing trip #" + selected.getId() + "...", false);

        new Thread(() -> {
            try {
                IncidentReportRequestModel req = new IncidentReportRequestModel();
                req.setTripId(selected.getId());
                req.setNotes(notes.isEmpty() ? "Trip completed normally" : notes);

                TripModel updated = ApiClient.getInstance().post("/api/driver/trips/" + selected.getId() + "/end", req, TripModel.class);
                Platform.runLater(() -> {
                    setTripActionStatus("Trip #" + updated.getId() + " Completed! Driver Status: AVAILABLE. TripLog created.", false);
                    incidentNotesArea.clear();
                    loadAssignedTrips();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setTripActionStatus("Error Ending Trip: " + e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    public void handleVerifyTicket(ActionEvent event) {
        TripModel selectedTrip = tripsTable.getSelectionModel().getSelectedItem();
        String qrData = qrInputArea.getText().trim();

        if (selectedTrip == null) {
            setQrVerifyResult("Please select an active trip before scanning tickets.", true);
            return;
        }

        if (qrData.isEmpty()) {
            setQrVerifyResult("Please paste or scan a ticket QR payload/number.", true);
            return;
        }

        new Thread(() -> {
            try {
                TicketVerificationRequestModel req = new TicketVerificationRequestModel(qrData, selectedTrip.getId());
                TicketVerificationResponseModel resp = ApiClient.getInstance().post("/api/driver/tickets/verify", req, TicketVerificationResponseModel.class);

                Platform.runLater(() -> {
                    if (resp.isValid()) {
                        setQrVerifyResult("SUCCESS: " + resp.getMessage() + " | Passenger: " + resp.getPassengerName() + " | Ticket #" + resp.getTicketNumber(), false);
                        qrInputArea.clear();
                    } else {
                        setQrVerifyResult("REJECTED: " + resp.getMessage(), true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> setQrVerifyResult("Verification Failed: " + e.getMessage(), true));
            }
        }).start();
    }

    private void setTripActionStatus(String msg, boolean isError) {
        tripActionStatusLabel.setText(msg);
        if (isError) {
            tripActionStatusLabel.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
        } else {
            tripActionStatusLabel.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold;");
        }
    }

    private void setQrVerifyResult(String msg, boolean isError) {
        qrVerifyResultLabel.setText(msg);
        if (isError) {
            qrVerifyResultLabel.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            qrVerifyResultLabel.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }
}
