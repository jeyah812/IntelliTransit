package com.intellitransit.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FareRuleDTO {

    private Long id;

    @NotNull(message = "Route ID is required")
    private Long routeId;

    @NotNull(message = "Minimum distance is required")
    @DecimalMin(value = "0.0", message = "Minimum distance cannot be negative")
    private BigDecimal minimumDistanceKm;

    @NotNull(message = "Maximum distance is required")
    @DecimalMin(value = "0.0", message = "Maximum distance cannot be negative")
    private BigDecimal maximumDistanceKm;

    @NotNull(message = "Base fare is required")
    @DecimalMin(value = "0.0", message = "Base fare cannot be negative")
    private BigDecimal baseFare;

    @DecimalMin(value = "0.0", message = "Student discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Student discount percentage cannot exceed 100")
    private BigDecimal studentDiscountPercentage;

    @DecimalMin(value = "0.0", message = "Senior discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Senior discount percentage cannot exceed 100")
    private BigDecimal seniorDiscountPercentage;

    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private boolean active = true;

    public FareRuleDTO() {}

    public FareRuleDTO(Long id, Long routeId, BigDecimal minimumDistanceKm, BigDecimal maximumDistanceKm, BigDecimal baseFare, BigDecimal studentDiscountPercentage, BigDecimal seniorDiscountPercentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        this.id = id;
        this.routeId = routeId;
        this.minimumDistanceKm = minimumDistanceKm;
        this.maximumDistanceKm = maximumDistanceKm;
        this.baseFare = baseFare;
        this.studentDiscountPercentage = studentDiscountPercentage;
        this.seniorDiscountPercentage = seniorDiscountPercentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }

    public static FareRuleDTOBuilder builder() {
        return new FareRuleDTOBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public BigDecimal getMinimumDistanceKm() { return minimumDistanceKm; }
    public void setMinimumDistanceKm(BigDecimal minimumDistanceKm) { this.minimumDistanceKm = minimumDistanceKm; }

    public BigDecimal getMaximumDistanceKm() { return maximumDistanceKm; }
    public void setMaximumDistanceKm(BigDecimal maximumDistanceKm) { this.maximumDistanceKm = maximumDistanceKm; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public BigDecimal getStudentDiscountPercentage() { return studentDiscountPercentage; }
    public void setStudentDiscountPercentage(BigDecimal studentDiscountPercentage) { this.studentDiscountPercentage = studentDiscountPercentage; }

    public BigDecimal getSeniorDiscountPercentage() { return seniorDiscountPercentage; }
    public void setSeniorDiscountPercentage(BigDecimal seniorDiscountPercentage) { this.seniorDiscountPercentage = seniorDiscountPercentage; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public static class FareRuleDTOBuilder {
        private Long id;
        private Long routeId;
        private BigDecimal minimumDistanceKm;
        private BigDecimal maximumDistanceKm;
        private BigDecimal baseFare;
        private BigDecimal studentDiscountPercentage;
        private BigDecimal seniorDiscountPercentage;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
        private boolean active = true;

        public FareRuleDTOBuilder id(Long id) { this.id = id; return this; }
        public FareRuleDTOBuilder routeId(Long routeId) { this.routeId = routeId; return this; }
        public FareRuleDTOBuilder minimumDistanceKm(BigDecimal minimumDistanceKm) { this.minimumDistanceKm = minimumDistanceKm; return this; }
        public FareRuleDTOBuilder maximumDistanceKm(BigDecimal maximumDistanceKm) { this.maximumDistanceKm = maximumDistanceKm; return this; }
        public FareRuleDTOBuilder baseFare(BigDecimal baseFare) { this.baseFare = baseFare; return this; }
        public FareRuleDTOBuilder studentDiscountPercentage(BigDecimal studentDiscountPercentage) { this.studentDiscountPercentage = studentDiscountPercentage; return this; }
        public FareRuleDTOBuilder seniorDiscountPercentage(BigDecimal seniorDiscountPercentage) { this.seniorDiscountPercentage = seniorDiscountPercentage; return this; }
        public FareRuleDTOBuilder effectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; return this; }
        public FareRuleDTOBuilder effectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; return this; }
        public FareRuleDTOBuilder active(boolean active) { this.active = active; return this; }

        public FareRuleDTO build() {
            return new FareRuleDTO(id, routeId, minimumDistanceKm, maximumDistanceKm, baseFare, studentDiscountPercentage, seniorDiscountPercentage, effectiveFrom, effectiveTo, active);
        }
    }
}
