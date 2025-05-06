package com.foodlist.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "is_checked", nullable = false)
    private boolean isChecked;

    @Column(name = "added_at", nullable = false)
    private LocalDate addedAt;

    @Column(name = "added_by_user_id")
    private Long addedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id")
    private ShoppingList shoppingList;

    public Item(String name, int quantity, boolean isChecked, LocalDate addedAt, Long addedByUserId) {
        this.name = name;
        this.quantity = quantity;
        this.isChecked = isChecked;
        this.addedAt = addedAt;
        this.addedByUserId = addedByUserId;
    }

}
