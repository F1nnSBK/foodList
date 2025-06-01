package com.foodlist.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.foodlist.service.model.ShoppingList}
 */
@Getter
@Setter
@AllArgsConstructor
public class ShoppingListDTO {
    Long id;
    String name;
    boolean isDefault;
    LocalDateTime createdAt;
}