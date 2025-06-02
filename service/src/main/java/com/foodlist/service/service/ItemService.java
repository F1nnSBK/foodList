package com.foodlist.service.service;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO;

import java.util.List;

public interface ItemService {
    ItemDTO addItem(ItemDTO itemDTO);

    List<ItemDisplayDTO> getAllItems();

    ItemDisplayDTO getItemById(Long id);

    ItemDTO updateItem(ItemDTO itemDTO);

    void deleteItemById(Long id);
}
