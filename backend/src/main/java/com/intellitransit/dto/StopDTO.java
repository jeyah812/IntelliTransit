package com.intellitransit.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class StopDTO {

    private Long id;

    @NotBlank(message = "Stop name is required")
    private String name;

    private BigDecimal latitude;
    private BigDecimal longitude;

    public StopDTO() {}

    public StopDTO(Long id, String name, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static StopDTOBuilder builder() {
        return new StopDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public static class StopDTOBuilder {
        private Long id;
        private String name;
        private BigDecimal latitude;
        private BigDecimal longitude;

        public StopDTOBuilder id(Long id) { this.id = id; return this; }
        public StopDTOBuilder name(String name) { this.name = name; return this; }
        public StopDTOBuilder latitude(BigDecimal latitude) { this.latitude = latitude; return this; }
        public StopDTOBuilder longitude(BigDecimal longitude) { this.longitude = longitude; return this; }

        public StopDTO build() {
            return new StopDTO(id, name, latitude, longitude);
        }
    }
}
