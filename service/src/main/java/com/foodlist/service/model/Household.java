package com.foodlist.service.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "households")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingList> shoppingLists = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public Household(String name) {
        this.name = name;
        this.createdAt = LocalDate.now();
    }

    public Household(String name, List<User> users) {
        this.name = name;
        this.createdAt = LocalDate.now();
        this.users.addAll(users);
    }

}
