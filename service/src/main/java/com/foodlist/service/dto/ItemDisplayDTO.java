package com.foodlist.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for displaying Item information in the UI, including names of related entities.
 * This DTO extends ItemDTO with additional fields for display purposes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDisplayDTO {

    private Long id;

    private String name;

    private int quantity;

    private boolean isChecked;

    private LocalDateTime addedAt;

    private Long addedByUserId;
    private String addedByUserName; // NEU: Benutzername des Hinzufügenden

    private Long shoppingListId;
    private String shoppingListName; // NEU: Name der Einkaufsliste

    // Optional: Konstruktor, um von ItemDTO zu ItemDisplayDTO zu konvertieren
    public ItemDisplayDTO(ItemDTO itemDTO) {
        this.id = itemDTO.getId();
        this.name = itemDTO.getName();
        this.quantity = itemDTO.getQuantity();
        this.isChecked = itemDTO.isChecked();
        this.addedAt = itemDTO.getAddedAt();
        this.addedByUserId = itemDTO.getAddedByUserId();
        this.shoppingListId = itemDTO.getShoppingListId();
        // addedByUserName und shoppingListName müssen separat gesetzt werden
    }
}
