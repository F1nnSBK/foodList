package com.foodlist.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * DTO for {@link com.foodlist.service.model.Item}
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemDTO{
    private Long id;

    String name;
    int quantity;
    boolean isChecked;

}