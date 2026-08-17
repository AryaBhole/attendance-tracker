package com.arya.attendance.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rollNumber;

    private String passwordHash; // null for Google-only accounts

    @Column(unique = true)
    private String email; // null for local-only accounts

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    public User() {}

    public User(String rollNumber, String passwordHash, String email, AuthProvider provider) {
        this.rollNumber = rollNumber;
        this.passwordHash = passwordHash;
        this.email = email;
        this.provider = provider;
    }

    public Long getId() { return id; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AuthProvider getProvider() { return provider; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }
}