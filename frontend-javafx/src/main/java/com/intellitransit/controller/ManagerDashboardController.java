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

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ManagerDashboardController {

    // Buses Tab
    @FXML private TableView<BusModel> busesTable;
    @FXML private TableColumn<BusModel, String> colBusReg;
    @FXML private TableColumn<BusModel, String> colBusNum;
    @FXML private TableColumn<BusModel, String> colBusModel;
    @FXML private TableColumn<BusModel, String> colBusCap;
    @FXML private TableColumn<BusModel, String> colBusStatus;

    @FXML private TextField busRegField;
    @FXML private TextField busNumField;
    @FXML private TextField busModelField;
    @FXML private TextField busCapField;
    @FXML private Label busStatusLabel;

    // Drivers Tab
    @FXML private TableView<DriverModel> driversTable;
    @FXML private TableColumn<DriverModel, String> colEmpId;
    @FXML private TableColumn<DriverModel, String> colDriverName;
    @FXML private TableColumn<DriverModel, String> colDriverPhone;
    @FXML private TableColumn<DriverModel, String> colDriverLicense;
    @FXML private TableColumn<DriverModel, String> colDriverStatus;

    // Routes Tab
    @FXML private TableView<RouteModel> routesTable;
    @FXML private TableColumn<RouteModel, String> colRouteNum;
    @FXML private TableColumn<RouteModel, String> colRouteName;
    @FXML private TableColumn<RouteModel, String> colRouteOrigin;
    @FXML private TableColumn<RouteModel, String> colRouteDest;
    @FXML private TableColumn<RouteModel, String> colRouteDist;

    @FXML private TextField routeNumField;
    @FXML private TextField routeNameField;
    @FXML private TextField routeOriginField;
    @FXML private TextField routeDestField;
    @FXML private TextField routeDistField;
    @FXML private TextField routeDurField;
    @FXML private Label routeStatusLabel;

    // Stops Form
    @FXML private TextField stopNameField;
    @FXML private TextField stopLatField;
    @FXML private TextField stopLngField;
    @FXML private Label stopStatusLabel;

    // Trip Scheduler Tab
    @FXML private ComboBox<RouteModel> schedRouteCombo;
    @FXML private ComboBox<BusModel> schedBusCombo;
    @FXML private ComboBox<DriverModel> schedDriverCombo;
    @FXML private TextField schedStartField;
    @FXML private TextField schedEndField;
    @FXML private Label schedStatusLabel;

    // Trip Logs Tab
    @FXML private TableView<TripLogModel> tripLogsTable;
    @FXML private TableColumn<TripLogModel, String> colLogId;
    @FXML private TableColumn<TripLogModel, String> colLogTrip;
    @FXML private TableColumn<TripLogModel, String> colLogRoute;
    @FXML private TableColumn<TripLogModel, String> colLogStart;
    @FXML private TableColumn<TripLogModel, String> colLogEnd;
    @FXML private TableColumn<TripLogModel, String> colLogDuration;
    @FXML private TableColumn<TripLogModel, String> colLogNotes;

    private final ObservableList<BusModel> busesList = FXCollections.observableArrayList();
    private final ObservableList<DriverModel> driversList = FXCollections.observableArrayList();
    private final ObservableList<RouteModel> routesList = FXCollections.observableArrayList();
    private final ObservableList<TripLogModel> tripLogsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        loadAllData();
    }

    private void setupTables() {
        // Buses Table
        colBusReg.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegistrationNumber()));
        colBusNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBusNumber()));
        colBusModel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getModel()));
        colBusCap.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCapacity())));
        colBusStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        busesTable.setItems(busesList);

        // Drivers Table
        colEmpId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmployeeId()));
        colDriverName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        colDriverPhone.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        colDriverLicense.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLicenseNumber()));
        colDriverStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        driversTable.setItems(driversList);

        // Routes Table
        colRouteNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRouteNumber()));
        colRouteName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRouteName()));
        colRouteOrigin.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrigin()));
        colRouteDest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDestination()));
        colRouteDist.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDistanceKm() + " km"));
        routesTable.setItems(routesList);

        // Trip Logs Table
        colLogId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colLogTrip.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTripId())));
        colLogRoute.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRouteNumber()));
        colLogStart.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getActualStart() != null ? d.getValue().getActualStart().toString() : ""));
        colLogEnd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getActualEnd() != null ? d.getValue().getActualEnd().toString() : ""));
        colLogDuration.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDurationMinutes() + " mins"));
        colLogNotes.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNotes()));
        tripLogsTable.setItems(tripLogsList);
    }

    private void loadAllData() {
        new Thread(() -> {
            try {
                BusModel[] buses = ApiClient.getInstance().get("/api/operations/buses", BusModel[].class);
                DriverModel[] drivers = ApiClient.getInstance().get("/api/operations/drivers", DriverModel[].class);
                RouteModel[] routes = ApiClient.getInstance().get("/api/routes", RouteModel[].class);
                TripLogModel[] logs = ApiClient.getInstance().get("/api/operations/trip-logs", TripLogModel[].class);

                Platform.runLater(() -> {
                    busesList.setAll(buses);
                    driversList.setAll(drivers);
                    routesList.setAll(routes);
                    tripLogsList.setAll(logs);

                    schedRouteCombo.setItems(routesList);
                    schedBusCombo.setItems(busesList);
                    schedDriverCombo.setItems(driversList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleAddBus(ActionEvent event) {
        String reg = busRegField.getText().trim();
        String num = busNumField.getText().trim();
        String model = busModelField.getText().trim();
        String capStr = busCapField.getText().trim();

        if (reg.isEmpty() || num.isEmpty() || capStr.isEmpty()) {
            setBusStatus("Please enter Registration Number, Bus Number, and Capacity.", true);
            return;
        }

        new Thread(() -> {
            try {
                BusModel req = new BusModel();
                req.setRegistrationNumber(reg);
                req.setBusNumber(num);
                req.setModel(model.isEmpty() ? "Standard Bus" : model);
                req.setCapacity(Integer.parseInt(capStr));
                req.setStatus("ACTIVE");

                ApiClient.getInstance().post("/api/operations/buses", req, BusModel.class);
                Platform.runLater(() -> {
                    setBusStatus("Bus registered successfully!", false);
                    busRegField.clear();
                    busNumField.clear();
                    busModelField.clear();
                    busCapField.clear();
                    loadAllData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setBusStatus("Error: " + e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    public void handleAddRoute(ActionEvent event) {
        String num = routeNumField.getText().trim();
        String name = routeNameField.getText().trim();
        String origin = routeOriginField.getText().trim();
        String dest = routeDestField.getText().trim();
        String distStr = routeDistField.getText().trim();
        String durStr = routeDurField.getText().trim();

        if (num.isEmpty() || name.isEmpty() || origin.isEmpty() || dest.isEmpty() || distStr.isEmpty()) {
            setRouteStatus("Please fill in all required route fields.", true);
            return;
        }

        new Thread(() -> {
            try {
                RouteModel req = new RouteModel();
                req.setRouteNumber(num);
                req.setRouteName(name);
                req.setOrigin(origin);
                req.setDestination(dest);
                req.setDistanceKm(new BigDecimal(distStr));
                req.setEstimatedDurationMinutes(durStr.isEmpty() ? 30 : Integer.parseInt(durStr));
                req.setActive(true);

                ApiClient.getInstance().post("/api/operations/routes", req, RouteModel.class);
                Platform.runLater(() -> {
                    setRouteStatus("Route added successfully!", false);
                    routeNumField.clear(); routeNameField.clear(); routeOriginField.clear(); routeDestField.clear(); routeDistField.clear(); routeDurField.clear();
                    loadAllData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setRouteStatus("Error: " + e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    public void handleAddStop(ActionEvent event) {
        String name = stopNameField.getText().trim();
        String latStr = stopLatField.getText().trim();
        String lngStr = stopLngField.getText().trim();

        if (name.isEmpty() || latStr.isEmpty() || lngStr.isEmpty()) {
            stopStatusLabel.setText("Please enter Stop Name, Latitude, and Longitude.");
            stopStatusLabel.setStyle("-fx-text-fill: #F87171;");
            return;
        }

        new Thread(() -> {
            try {
                StopModel req = new StopModel();
                req.setName(name);
                req.setLatitude(Double.parseDouble(latStr));
                req.setLongitude(Double.parseDouble(lngStr));

                ApiClient.getInstance().post("/api/operations/stops", req, StopModel.class);
                Platform.runLater(() -> {
                    stopStatusLabel.setText("Stop added successfully!");
                    stopStatusLabel.setStyle("-fx-text-fill: #34D399;");
                    stopNameField.clear(); stopLatField.clear(); stopLngField.clear();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    stopStatusLabel.setText("Error: " + e.getMessage());
                    stopStatusLabel.setStyle("-fx-text-fill: #F87171;");
                });
            }
        }).start();
    }

    @FXML
    public void handleScheduleTrip(ActionEvent event) {
        RouteModel route = schedRouteCombo.getValue();
        BusModel bus = schedBusCombo.getValue();
        DriverModel driver = schedDriverCombo.getValue();

        if (route == null || bus == null || driver == null) {
            setSchedStatus("Please select Route, Bus, and Driver.", true);
            return;
        }

        new Thread(() -> {
            try {
                LocalDateTime now = LocalDateTime.now();
                TripScheduleRequestModel req = new TripScheduleRequestModel(
                        route.getId(), bus.getId(), driver.getId(),
                        now.plusMinutes(15), now.plusHours(2)
                );

                ApiClient.getInstance().post("/api/operations/trips/schedule", req, TripModel.class);
                Platform.runLater(() -> {
                    setSchedStatus("Trip scheduled successfully for Route " + route.getRouteNumber() + "!", false);
                    loadAllData();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setSchedStatus("Scheduling Error: " + e.getMessage(), true));
            }
        }).start();
    }

    private void setBusStatus(String msg, boolean isError) {
        busStatusLabel.setText(msg);
        busStatusLabel.setStyle(isError ? "-fx-text-fill: #F87171;" : "-fx-text-fill: #34D399;");
    }

    private void setRouteStatus(String msg, boolean isError) {
        routeStatusLabel.setText(msg);
        routeStatusLabel.setStyle(isError ? "-fx-text-fill: #F87171;" : "-fx-text-fill: #34D399;");
    }

    private void setSchedStatus(String msg, boolean isError) {
        schedStatusLabel.setText(msg);
        schedStatusLabel.setStyle(isError ? "-fx-text-fill: #F87171;" : "-fx-text-fill: #34D399;");
    }
}
