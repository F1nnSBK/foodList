package com.foodlist.service.service;

import com.foodlist.service.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO addItem(ItemDTO itemDTO);

    List<ItemDTO> getAllItems();

    ItemDTO getItemById(Long id);

    ItemDTO updateItem(ItemDTO itemDTO);

    void deleteItemById(Long id);
}
