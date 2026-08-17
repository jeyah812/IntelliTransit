package com.intellitransit.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fare_rules")
public class FareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "minimum_distance_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal minimumDistanceKm;

    @Column(name = "maximum_distance_km", nullable = false, precision = 6, scale = 2)
    private BigDecimal maximumDistanceKm;

    @Column(name = "base_fare", nullable = false, precision = 6, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "student_discount_percentage", precision = 5, scale = 2)
    private BigDecimal studentDiscountPercentage;

    @Column(name = "senior_discount_percentage", precision = 5, scale = 2)
    private BigDecimal seniorDiscountPercentage;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private boolean active = true;

    public FareRule() {}

    public FareRule(Long id, Route route, BigDecimal minimumDistanceKm, BigDecimal maximumDistanceKm, BigDecimal baseFare, BigDecimal studentDiscountPercentage, BigDecimal seniorDiscountPercentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        this.id = id;
        this.route = route;
        this.minimumDistanceKm = minimumDistanceKm;
        this.maximumDistanceKm = maximumDistanceKm;
        this.baseFare = baseFare;
        this.studentDiscountPercentage = studentDiscountPercentage;
        this.seniorDiscountPercentage = seniorDiscountPercentage;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.active = active;
    }

    public static FareRuleBuilder builder() {
        return new FareRuleBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

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

    public static class FareRuleBuilder {
        private Long id;
        private Route route;
        private BigDecimal minimumDistanceKm;
        private BigDecimal maximumDistanceKm;
        private BigDecimal baseFare;
        private BigDecimal studentDiscountPercentage;
        private BigDecimal seniorDiscountPercentage;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;
        private boolean active = true;

        public FareRuleBuilder id(Long id) { this.id = id; return this; }
        public FareRuleBuilder route(Route route) { this.route = route; return this; }
        public FareRuleBuilder minimumDistanceKm(BigDecimal minimumDistanceKm) { this.minimumDistanceKm = minimumDistanceKm; return this; }
        public FareRuleBuilder maximumDistanceKm(BigDecimal maximumDistanceKm) { this.maximumDistanceKm = maximumDistanceKm; return this; }
        public FareRuleBuilder baseFare(BigDecimal baseFare) { this.baseFare = baseFare; return this; }
        public FareRuleBuilder studentDiscountPercentage(BigDecimal studentDiscountPercentage) { this.studentDiscountPercentage = studentDiscountPercentage; return this; }
        public FareRuleBuilder seniorDiscountPercentage(BigDecimal seniorDiscountPercentage) { this.seniorDiscountPercentage = seniorDiscountPercentage; return this; }
        public FareRuleBuilder effectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; return this; }
        public FareRuleBuilder effectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; return this; }
        public FareRuleBuilder active(boolean active) { this.active = active; return this; }

        public FareRule build() {
            return new FareRule(id, route, minimumDistanceKm, maximumDistanceKm, baseFare, studentDiscountPercentage, seniorDiscountPercentage, effectiveFrom, effectiveTo, active);
        }
    }
}
