package ru.utmn.chamortsev.netflix.security;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private LocalDate birthday;
    private String password;
    private boolean enabled = true;
    private String role = "USER";

    @Column(insertable = true, updatable = false)
    private LocalDateTime created;
    @Column(name = "modified")
    private LocalDateTime updated;

    public Person() {
    }

    public Person(String email, String name, LocalDate birthday, String password, boolean enabled, String role) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public Person(String email, String name, LocalDate birthday, String password) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.password = password;
    }

    @PrePersist
    void onCreate() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updated = LocalDateTime.now();
    }
}
