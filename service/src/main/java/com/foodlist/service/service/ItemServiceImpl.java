package com.foodlist.service.service;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO; // Importieren Sie das neue DTO
import com.foodlist.service.mapper.ItemMapper;
import com.foodlist.service.model.Item;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.ItemRepo;
import com.foodlist.service.repository.ShoppingListRepo;
import com.foodlist.service.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepo itemRepo;
    private final UserRepo userRepo;
    private final ShoppingListRepo shoppingListRepo;

    public ItemServiceImpl(ItemMapper itemMapper, ItemRepo itemRepo,
                           UserRepo userRepo, ShoppingListRepo shoppingListRepo) {
        this.itemMapper = itemMapper;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.shoppingListRepo = shoppingListRepo;
    }

    @Override
    public ItemDTO addItem(ItemDTO itemDTO) {
        try {
            Item item = itemMapper.itemDTOToItem(itemDTO, userRepo, shoppingListRepo); // Pass repos to mapper

            item.setAddedAt(LocalDateTime.now());

            if (itemDTO.getAddedByUserId() != null && item.getAddedBy() == null) {
                throw new EntityNotFoundException(
                        "User with ID " + itemDTO.getAddedByUserId() + " not found. Cannot add item.");
            } else if (itemDTO.getAddedByUserId() == null && item.getAddedBy() != null) {
                item.setAddedBy(null);
            }

            if (itemDTO.getShoppingListId() != null && item.getShoppingList() == null) {
                throw new EntityNotFoundException(
                        "ShoppingList with ID " + itemDTO.getShoppingListId() + " not found. Cannot add item.");
            } else if (itemDTO.getShoppingListId() == null && item.getShoppingList() != null) {
                item.setShoppingList(null);
            }

            Item savedItem = itemRepo.save(item);
            return itemMapper.itemToItemDTO(savedItem); // Return original ItemDTO for API consistency
        } catch (Exception e) {
            log.error("Error adding item: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Ruft alle Items ab und konvertiert sie in ItemDisplayDTOs für die UI-Anzeige.
     *
     * @return Eine Liste von ItemDisplayDTOs.
     */
    @Override
    public List<ItemDisplayDTO> getAllItems() { // Rückgabetyp geändert
        List<Item> items = itemRepo.findAll();
        // Konvertiere die Item-Entitäten in ItemDisplayDTOs
        return itemMapper.itemsToItemDisplayDTOs(items);
    }

    /**
     * Ruft ein Item anhand seiner ID ab und konvertiert es in ItemDisplayDTO für die UI-Anzeige.
     *
     * @param id Die ID des abzurufenden Items.
     * @return Das ItemDisplayDTO des gefundenen Items.
     * @throws EntityNotFoundException wenn kein Item mit der gegebenen ID existiert.
     */
    @Override
    public ItemDisplayDTO getItemById(Long id) { // Rückgabetyp geändert
        Item item = itemRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item with ID " + id + " not found."));
        // Konvertiere die Item-Entität in ein ItemDisplayDTO
        return itemMapper.itemToItemDisplayDTO(item);
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDTO) {
        Item existingItem = itemRepo.findById(itemDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Item to be updated not found. Item Id: " + itemDTO.getId()));

        // Aktualisiere grundlegende Felder
        existingItem.setName(itemDTO.getName());
        existingItem.setQuantity(itemDTO.getQuantity());
        existingItem.setChecked(itemDTO.isChecked());
        existingItem.setAddedAt(LocalDateTime.now());

        // Behandle User relationship update
        if (itemDTO.getAddedByUserId() != null) {
            User user = userRepo.findById(itemDTO.getAddedByUserId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User with ID " + itemDTO.getAddedByUserId() + " not found for update."));
            existingItem.setAddedBy(user);
        } else {
            existingItem.setAddedBy(null);
        }

        // Behandle ShoppingList relationship update
        if (itemDTO.getShoppingListId() != null) {
            ShoppingList shoppingList = shoppingListRepo.findById(itemDTO.getShoppingListId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "ShoppingList with ID " + itemDTO.getShoppingListId() + " not found for update."));
            existingItem.setShoppingList(shoppingList);
        } else {
            existingItem.setShoppingList(null);
        }

        Item updatedItem = itemRepo.save(existingItem);
        return itemMapper.itemToItemDTO(updatedItem); // Return original ItemDTO for API consistency
    }

    @Override
    public void deleteItemById(Long id) {
        if (!itemRepo.existsById(id)) {
            throw new EntityNotFoundException("Item with ID " + id + " not found for deletion.");
        }
        itemRepo.deleteById(id);
    }
}
