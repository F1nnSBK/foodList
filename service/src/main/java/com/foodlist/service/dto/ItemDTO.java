package com.foodlist.service.dto;

import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * DTO for {@link com.foodlist.service.model.Item}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO{

    private Long id;

    private String name;

    private int quantity;

    private boolean isChecked;

    private LocalDateTime addedAt;

    private Long addedByUserId;

    private Long shoppingListId;
}