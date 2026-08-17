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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PassengerDashboardController {

    // Route & Fare Tab
    @FXML private ComboBox<RouteModel> routeComboBox;
    @FXML private ComboBox<RouteStopModel> originStopCombo;
    @FXML private ComboBox<RouteStopModel> destStopCombo;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private Label baseFareLabel;
    @FXML private Label discountLabel;
    @FXML private Label finalFareLabel;

    // Trip & Booking
    @FXML private ComboBox<TripModel> tripComboBox;
    @FXML private Label bookingStatusLabel;

    // My Bookings & QR Display
    @FXML private TableView<BookingResponseModel> bookingsTable;
    @FXML private TableColumn<BookingResponseModel, String> colRef;
    @FXML private TableColumn<BookingResponseModel, String> colRoute;
    @FXML private TableColumn<BookingResponseModel, String> colFrom;
    @FXML private TableColumn<BookingResponseModel, String> colTo;
    @FXML private TableColumn<BookingResponseModel, String> colFare;
    @FXML private TableColumn<BookingResponseModel, String> colStatus;

    @FXML private ImageView qrImageView;
    @FXML private Label qrTicketNumLabel;
    @FXML private Label qrStatusLabel;

    // Complaint Tab
    @FXML private TextField complaintSubjectField;
    @FXML private ComboBox<String> complaintCategoryCombo;
    @FXML private TextArea complaintDescArea;
    @FXML private Label complaintStatusLabel;

    private final ObservableList<RouteModel> routesList = FXCollections.observableArrayList();
    private final ObservableList<TripModel> tripsList = FXCollections.observableArrayList();
    private final ObservableList<BookingResponseModel> bookingsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList("STANDARD", "STUDENT", "SENIOR"));
        categoryCombo.getSelectionModel().select("STANDARD");

        complaintCategoryCombo.setItems(FXCollections.observableArrayList("SERVICE_DELAY", "FARES_OVERCHARGE", "DRIVER_BEHAVIOR", "BUS_CONDITION", "OTHER"));
        complaintCategoryCombo.getSelectionModel().select("SERVICE_DELAY");

        setupTableColumns();
        loadRoutes();
        loadMyBookings();

        routeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                onRouteSelected(newVal);
            }
        });

        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getTicket() != null) {
                displayTicketQr(newVal.getTicket());
            }
        });
    }

    private void setupTableColumns() {
        colRef.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookingReference()));
        colRoute.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRouteNumber()));
        colFrom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOriginStop()));
        colTo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestinationStop()));
        colFare.setCellValueFactory(data -> new SimpleStringProperty("$" + data.getValue().getFareAmount()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookingStatus()));

        bookingsTable.setItems(bookingsList);
    }

    private void loadRoutes() {
        new Thread(() -> {
            try {
                RouteModel[] routes = ApiClient.getInstance().get("/api/routes", RouteModel[].class);
                TripModel[] trips = ApiClient.getInstance().get("/api/operations/trips", TripModel[].class);

                Platform.runLater(() -> {
                    routesList.setAll(routes);
                    routeComboBox.setItems(routesList);

                    tripsList.setAll(trips);
                    tripComboBox.setItems(tripsList);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void onRouteSelected(RouteModel route) {
        if (route.getStops() != null) {
            ObservableList<RouteStopModel> stops = FXCollections.observableArrayList(route.getStops());
            originStopCombo.setItems(stops);
            destStopCombo.setItems(stops);
        }
    }

    @FXML
    public void handleCalculateFare(ActionEvent event) {
        RouteModel route = routeComboBox.getValue();
        RouteStopModel origin = originStopCombo.getValue();
        RouteStopModel dest = destStopCombo.getValue();
        String category = categoryCombo.getValue();

        if (route == null || origin == null || dest == null) {
            setBookingStatus("Please select Route, Origin, and Destination stops.", true);
            return;
        }

        new Thread(() -> {
            try {
                FareCalculationRequestModel req = new FareCalculationRequestModel(route.getId(), origin.getStopId(), dest.getStopId(), category);
                FareCalculationResponseModel resp = ApiClient.getInstance().post("/api/fares/calculate", req, FareCalculationResponseModel.class);

                Platform.runLater(() -> {
                    baseFareLabel.setText("$" + resp.getBaseFare());
                    discountLabel.setText(resp.getDiscountPercentage() + "%");
                    finalFareLabel.setText("$" + resp.getFinalFare());
                    setBookingStatus("Fare calculated successfully for " + resp.getDistanceKm() + " km.", false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> setBookingStatus("Fare Calculation Error: " + e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    public void handleBookTicket(ActionEvent event) {
        TripModel trip = tripComboBox.getValue();
        RouteStopModel origin = originStopCombo.getValue();
        RouteStopModel dest = destStopCombo.getValue();
        String category = categoryCombo.getValue();

        if (trip == null || origin == null || dest == null) {
            setBookingStatus("Please select Trip, Origin, and Destination stops.", true);
            return;
        }

        setBookingStatus("Processing booking...", false);

        new Thread(() -> {
            try {
                BookingRequestModel req = new BookingRequestModel(trip.getId(), origin.getStopId(), dest.getStopId(), category);
                BookingResponseModel resp = ApiClient.getInstance().post("/api/passenger/bookings", req, BookingResponseModel.class);

                Platform.runLater(() -> {
                    setBookingStatus("Booking Confirmed! Ref: " + resp.getBookingReference(), false);
                    loadMyBookings();
                });
            } catch (Exception e) {
                Platform.runLater(() -> setBookingStatus("Booking Error: " + e.getMessage(), true));
            }
        }).start();
    }

    private void loadMyBookings() {
        new Thread(() -> {
            try {
                BookingResponseModel[] bookings = ApiClient.getInstance().get("/api/passenger/bookings/my", BookingResponseModel[].class);
                Platform.runLater(() -> {
                    bookingsList.setAll(bookings);
                    if (!bookingsList.isEmpty() && bookingsList.get(0).getTicket() != null) {
                        displayTicketQr(bookingsList.get(0).getTicket());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayTicketQr(TicketModel ticket) {
        qrTicketNumLabel.setText("Ticket #" + ticket.getTicketNumber());
        qrStatusLabel.setText("Status: " + ticket.getStatus());

        new Thread(() -> {
            try {
                byte[] imageBytes = ApiClient.getInstance().fetchImageBytes("/api/tickets/" + ticket.getTicketId() + "/qr-image?size=250");
                Image img = new Image(new ByteArrayInputStream(imageBytes));
                Platform.runLater(() -> qrImageView.setImage(img));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handleSubmitComplaint(ActionEvent event) {
        String subject = complaintSubjectField.getText().trim();
        String category = complaintCategoryCombo.getValue();
        String desc = complaintDescArea.getText().trim();

        if (subject.isEmpty() || desc.isEmpty()) {
            complaintStatusLabel.setText("Please enter both subject and description.");
            complaintStatusLabel.setStyle("-fx-text-fill: #F87171;");
            return;
        }

        new Thread(() -> {
            try {
                ComplaintRequestModel req = new ComplaintRequestModel(null, subject, desc, category);
                ApiClient.getInstance().post("/api/passenger/complaints", req, ComplaintModel.class);

                Platform.runLater(() -> {
                    complaintStatusLabel.setText("Complaint submitted successfully.");
                    complaintStatusLabel.setStyle("-fx-text-fill: #34D399;");
                    complaintSubjectField.clear();
                    complaintDescArea.clear();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    complaintStatusLabel.setText("Failed: " + e.getMessage());
                    complaintStatusLabel.setStyle("-fx-text-fill: #F87171;");
                });
            }
        }).start();
    }

    private void setBookingStatus(String msg, boolean isError) {
        bookingStatusLabel.setText(msg);
        if (isError) {
            bookingStatusLabel.setStyle("-fx-text-fill: #F87171; -fx-font-weight: bold;");
        } else {
            bookingStatusLabel.setStyle("-fx-text-fill: #34D399; -fx-font-weight: bold;");
        }
    }
}
