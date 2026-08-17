package com.intellitransit.dto;

import java.math.BigDecimal;

public class FareCalculationResponse {

    private Long routeId;
    private String originStopName;
    private String destinationStopName;
    private BigDecimal distanceKm;
    private BigDecimal baseFare;
    private BigDecimal discountPercentage;
    private BigDecimal finalFare;
    private String passengerCategory;

    public FareCalculationResponse() {}

    public FareCalculationResponse(Long routeId, String originStopName, String destinationStopName, BigDecimal distanceKm, BigDecimal baseFare, BigDecimal discountPercentage, BigDecimal finalFare, String passengerCategory) {
        this.routeId = routeId;
        this.originStopName = originStopName;
        this.destinationStopName = destinationStopName;
        this.distanceKm = distanceKm;
        this.baseFare = baseFare;
        this.discountPercentage = discountPercentage;
        this.finalFare = finalFare;
        this.passengerCategory = passengerCategory;
    }

    public static FareCalculationResponseBuilder builder() {
        return new FareCalculationResponseBuilder();
    }

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

    public static class FareCalculationResponseBuilder {
        private Long routeId;
        private String originStopName;
        private String destinationStopName;
        private BigDecimal distanceKm;
        private BigDecimal baseFare;
        private BigDecimal discountPercentage;
        private BigDecimal finalFare;
        private String passengerCategory;

        public FareCalculationResponseBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public FareCalculationResponseBuilder originStopName(String originStopName) { this.originStopName = originStopName; return this; }
        public FareCalculationResponseBuilder destinationStopName(String destinationStopName) { this.destinationStopName = destinationStopName; return this; }
        public FareCalculationResponseBuilder distanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; return this; }
        public FareCalculationResponseBuilder baseFare(BigDecimal baseFare) { this.baseFare = baseFare; return this; }
        public FareCalculationResponseBuilder discountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; return this; }
        public FareCalculationResponseBuilder finalFare(BigDecimal finalFare) { this.finalFare = finalFare; return this; }
        public FareCalculationResponseBuilder passengerCategory(String passengerCategory) { this.passengerCategory = passengerCategory; return this; }

        public FareCalculationResponse build() {
            return new FareCalculationResponse(routeId, originStopName, destinationStopName, distanceKm, baseFare, discountPercentage, finalFare, passengerCategory);
        }
    }
}
