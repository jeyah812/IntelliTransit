package com.intellitransit.dto;

import com.intellitransit.entity.enums.BusStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BusDTO {

    private Long id;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Bus number is required")
    private String busNumber;

    private String model;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private BusStatus status = BusStatus.ACTIVE;

    public BusDTO() {}

    public BusDTO(Long id, String registrationNumber, String busNumber, String model, Integer capacity, BusStatus status) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.busNumber = busNumber;
        this.model = model;
        this.capacity = capacity;
        this.status = status != null ? status : BusStatus.ACTIVE;
    }

    public static BusDTOBuilder builder() {
        return new BusDTOBuilder();
    }

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

    public BusStatus getStatus() { return status; }
    public void setStatus(BusStatus status) { this.status = status; }

    public static class BusDTOBuilder {
        private Long id;
        private String registrationNumber;
        private String busNumber;
        private String model;
        private Integer capacity;
        private BusStatus status = BusStatus.ACTIVE;

        public BusDTOBuilder id(Long id) { this.id = id; return this; }
        public BusDTOBuilder registrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; return this; }
        public BusDTOBuilder busNumber(String busNumber) { this.busNumber = busNumber; return this; }
        public BusDTOBuilder model(String model) { this.model = model; return this; }
        public BusDTOBuilder capacity(Integer capacity) { this.capacity = capacity; return this; }
        public BusDTOBuilder status(BusStatus status) { this.status = status; return this; }

        public BusDTO build() {
            return new BusDTO(id, registrationNumber, busNumber, model, capacity, status);
        }
    }
}
