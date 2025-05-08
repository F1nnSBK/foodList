package com.foodlist.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @OneToMany(mappedBy = "addedBy")
    List<Item> itemsAdded = new ArrayList<>();

    @Column(name = "name")
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public User(String username, String passwordHash, boolean enabled) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.enabled = enabled;
        this.createdAt = LocalDate.now();
    }

    public User(String username, String passwordHash, Household household, boolean enabled) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.household = household;
        this.enabled = enabled;
        this.createdAt = LocalDate.now();
    }

}
