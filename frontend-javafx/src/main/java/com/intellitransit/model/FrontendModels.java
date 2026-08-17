package com.intellitransit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FrontendModels {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthResponseModel {
        private String accessToken;
        private String tokenType;
        private Long userId;
        private String username;
        private String email;
        private String role;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class LoginRequestModel {
        private String username;
        private String password;

        public LoginRequestModel() {}
        public LoginRequestModel(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequestModel {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private String phone;
        private String role;

        public RegisterRequestModel() {}
        public RegisterRequestModel(String username, String email, String password, String fullName, String phone, String role) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.phone = phone;
            this.role = role;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusModel {
        private Long id;
        private String registrationNumber;
        private String busNumber;
        private String model;
        private Integer capacity;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getBusNumber() { return busNumber; }
        public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() { return busNumber + " (" + registrationNumber + ")"; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriverModel {
        private Long id;
        private Long userId;
        private String employeeId;
        private String fullName;
        private String phone;
        private String licenseNumber;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() { return fullName + " [" + employeeId + "] (" + status + ")"; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteModel {
        private Long id;
        private String routeNumber;
        private String routeName;
        private String origin;
        private String destination;
        private BigDecimal distanceKm;
        private Integer estimatedDurationMinutes;
        private boolean active;
        private List<RouteStopModel> stops;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public BigDecimal getDistanceKm() { return distanceKm; }
        public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
        public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
        public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public List<RouteStopModel> getStops() { return stops; }
        public void setStops(List<RouteStopModel> stops) { this.stops = stops; }

        @Override
        public String toString() { return routeNumber + " - " + routeName + " (" + origin + " ➔ " + destination + ")"; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StopModel {
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        @Override
        public String toString() { return name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RouteStopModel {
        private Long id;
        private Long routeId;
        private Long stopId;
        private String stopName;
        private Integer stopSequence;
        private BigDecimal distanceFromOriginKm;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public Long getStopId() { return stopId; }
        public void setStopId(Long stopId) { this.stopId = stopId; }
        public String getStopName() { return stopName; }
        public void setStopName(String stopName) { this.stopName = stopName; }
        public Integer getStopSequence() { return stopSequence; }
        public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }
        public BigDecimal getDistanceFromOriginKm() { return distanceFromOriginKm; }
        public void setDistanceFromOriginKm(BigDecimal distanceFromOriginKm) { this.distanceFromOriginKm = distanceFromOriginKm; }

        @Override
        public String toString() { return stopSequence + ". " + stopName + " (" + distanceFromOriginKm + " km)"; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TripModel {
        private Long id;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private Long busId;
        private String busNumber;
        private Long driverId;
        private String driverName;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }
        public String getBusNumber() { return busNumber; }
        public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
        public Long getDriverId() { return driverId; }
        public void setDriverId(Long driverId) { this.driverId = driverId; }
        public String getDriverName() { return driverName; }
        public void setDriverName(String driverName) { this.driverName = driverName; }
        public LocalDateTime getScheduledStart() { return scheduledStart; }
        public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
        public LocalDateTime getScheduledEnd() { return scheduledEnd; }
        public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
        public LocalDateTime getActualStart() { return actualStart; }
        public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }
        public LocalDateTime getActualEnd() { return actualEnd; }
        public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public String toString() { return "Trip #" + id + " [" + routeNumber + "] " + busNumber + " (" + status + ")"; }
    }

    public static class IncidentReportRequestModel {
        private Long tripId;
        private String notes;

        public IncidentReportRequestModel() {}
        public IncidentReportRequestModel(Long tripId, String notes) {
            this.tripId = tripId;
            this.notes = notes;
        }

        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class FareCalculationRequestModel {
        private Long routeId;
        private Long originStopId;
        private Long destinationStopId;
        private String passengerCategory;

        public FareCalculationRequestModel() {}
        public FareCalculationRequestModel(Long routeId, Long originStopId, Long destinationStopId, String passengerCategory) {
            this.routeId = routeId;
            this.originStopId = originStopId;
            this.destinationStopId = destinationStopId;
            this.passengerCategory = passengerCategory;
        }

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public Long getOriginStopId() { return originStopId; }
        public void setOriginStopId(Long originStopId) { this.originStopId = originStopId; }
        public Long getDestinationStopId() { return destinationStopId; }
        public void setDestinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; }
        public String getPassengerCategory() { return passengerCategory; }
        public void setPassengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareCalculationResponseModel {
        private Long routeId;
        private String originStopName;
        private String destinationStopName;
        private BigDecimal distanceKm;
        private BigDecimal baseFare;
        private BigDecimal discountPercentage;
        private BigDecimal finalFare;
        private String passengerCategory;

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public String getOriginStopName() { return originStopName; }
        public void setOriginStopName(String originStopName) { this.originStopName = originStopName; }
        public String getDestinationStopName() { return destinationStopName; }
        public void setDestinationStopName(String destinationStopName) { this.destinationStopName = destinationStopName; }
        public BigDecimal getDistanceKm() { return distanceKm; }
        public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }
        public BigDecimal getBaseFare() { return baseFare; }
        public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public BigDecimal getFinalFare() { return finalFare; }
        public void setFinalFare(BigDecimal finalFare) { this.finalFare = finalFare; }
        public String getPassengerCategory() { return passengerCategory; }
        public void setPassengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; }
    }

    public static class BookingRequestModel {
        private Long tripId;
        private Long originStopId;
        private Long destinationStopId;
        private String passengerCategory;

        public BookingRequestModel() {}
        public BookingRequestModel(Long tripId, Long originStopId, Long destinationStopId, String passengerCategory) {
            this.tripId = tripId;
            this.originStopId = originStopId;
            this.destinationStopId = destinationStopId;
            this.passengerCategory = passengerCategory;
        }

        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public Long getOriginStopId() { return originStopId; }
        public void setOriginStopId(Long originStopId) { this.originStopId = originStopId; }
        public Long getDestinationStopId() { return destinationStopId; }
        public void setDestinationStopId(Long destinationStopId) { this.destinationStopId = destinationStopId; }
        public String getPassengerCategory() { return passengerCategory; }
        public void setPassengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BookingResponseModel {
        private Long bookingId;
        private String bookingReference;
        private String routeNumber;
        private String originStop;
        private String destinationStop;
        private BigDecimal fareAmount;
        private String paymentStatus;
        private String bookingStatus;
        private LocalDateTime bookingTime;
        private TicketModel ticket;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public String getOriginStop() { return originStop; }
        public void setOriginStop(String originStop) { this.originStop = originStop; }
        public String getDestinationStop() { return destinationStop; }
        public void setDestinationStop(String destinationStop) { this.destinationStop = destinationStop; }
        public BigDecimal getFareAmount() { return fareAmount; }
        public void setFareAmount(BigDecimal fareAmount) { this.fareAmount = fareAmount; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getBookingStatus() { return bookingStatus; }
        public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
        public LocalDateTime getBookingTime() { return bookingTime; }
        public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }
        public TicketModel getTicket() { return ticket; }
        public void setTicket(TicketModel ticket) { this.ticket = ticket; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketModel {
        private Long ticketId;
        private String ticketNumber;
        private String qrData;
        private LocalDateTime issuedAt;
        private LocalDateTime verifiedAt;
        private String status;

        public Long getTicketId() { return ticketId; }
        public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getQrData() { return qrData; }
        public void setQrData(String qrData) { this.qrData = qrData; }
        public LocalDateTime getIssuedAt() { return issuedAt; }
        public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
        public LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class TicketVerificationRequestModel {
        private String ticketNumberOrQrData;
        private Long tripId;

        public TicketVerificationRequestModel() {}
        public TicketVerificationRequestModel(String ticketNumberOrQrData, Long tripId) {
            this.ticketNumberOrQrData = ticketNumberOrQrData;
            this.tripId = tripId;
        }

        public String getTicketNumberOrQrData() { return ticketNumberOrQrData; }
        public void setTicketNumberOrQrData(String ticketNumberOrQrData) { this.ticketNumberOrQrData = ticketNumberOrQrData; }
        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TicketVerificationResponseModel {
        private boolean valid;
        private String message;
        private String ticketNumber;
        private String bookingReference;
        private String passengerName;
        private LocalDateTime verifiedAt;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getTicketNumber() { return ticketNumber; }
        public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TripLogModel {
        private Long id;
        private Long tripId;
        private String routeNumber;
        private LocalDateTime actualStart;
        private LocalDateTime actualEnd;
        private Integer durationMinutes;
        private LocalDateTime recordedAt;
        private String notes;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        public LocalDateTime getActualStart() { return actualStart; }
        public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }
        public LocalDateTime getActualEnd() { return actualEnd; }
        public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        public LocalDateTime getRecordedAt() { return recordedAt; }
        public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ComplaintRequestModel {
        private Long tripId;
        private String subject;
        private String description;
        private String category;

        public ComplaintRequestModel() {}
        public ComplaintRequestModel(Long tripId, String subject, String description, String category) {
            this.tripId = tripId;
            this.subject = subject;
            this.description = description;
            this.category = category;
        }

        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ComplaintModel {
        private Long id;
        private Long passengerId;
        private String passengerName;
        private Long tripId;
        private String subject;
        private String description;
        private String category;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPassengerId() { return passengerId; }
        public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
        public String getPassengerName() { return passengerName; }
        public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
        public Long getTripId() { return tripId; }
        public void setTripId(Long tripId) { this.tripId = tripId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    }

    public static class TripScheduleRequestModel {
        private Long routeId;
        private Long busId;
        private Long driverId;
        private LocalDateTime scheduledStart;
        private LocalDateTime scheduledEnd;

        public TripScheduleRequestModel() {}
        public TripScheduleRequestModel(Long routeId, Long busId, Long driverId, LocalDateTime scheduledStart, LocalDateTime scheduledEnd) {
            this.routeId = routeId;
            this.busId = busId;
            this.driverId = driverId;
            this.scheduledStart = scheduledStart;
            this.scheduledEnd = scheduledEnd;
        }

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }
        public Long getDriverId() { return driverId; }
        public void setDriverId(Long driverId) { this.driverId = driverId; }
        public LocalDateTime getScheduledStart() { return scheduledStart; }
        public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
        public LocalDateTime getScheduledEnd() { return scheduledEnd; }
        public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
    }
}
