package com.foodlist.service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.foodlist.service.model.ShoppingList}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDTO {
    private Long id;

    private String name;

    private boolean isDefault;

    private LocalDateTime createdAt;

    private Long householdId;

    private List<ItemDTO> items;
}