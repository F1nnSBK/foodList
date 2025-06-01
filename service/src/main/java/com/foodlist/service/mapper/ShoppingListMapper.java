package com.foodlist.service.mapper;

import com.foodlist.service.dto.ShoppingListDTO;
import com.foodlist.service.model.ShoppingList;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingListMapper {

    ShoppingList shoppingListDTOToShoppingList(ShoppingListDTO shoppingListDTO);
    ShoppingListDTO shoppingListToShoppingListDTO(ShoppingList shoppingList);

}
