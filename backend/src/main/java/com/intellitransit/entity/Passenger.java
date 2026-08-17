package com.intellitransit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Passenger() {}

    public Passenger(Long id, User user, String fullName, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static PassengerBuilder builder() {
        return new PassengerBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class PassengerBuilder {
        private Long id;
        private User user;
        private String fullName;
        private String phone;
        private LocalDateTime createdAt;

        public PassengerBuilder id(Long id) { this.id = id; return this; }
        public PassengerBuilder user(User user) { this.user = user; return this; }
        public PassengerBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public PassengerBuilder phone(String phone) { this.phone = phone; return this; }
        public PassengerBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Passenger build() {
            return new Passenger(id, user, fullName, phone, createdAt);
        }
    }
}
