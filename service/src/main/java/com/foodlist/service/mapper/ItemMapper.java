package com.foodlist.service.mapper;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO; // Importieren Sie das neue DTO
import com.foodlist.service.model.Item;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.ShoppingListRepo;
import com.foodlist.service.repository.UserRepo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

/**
 * MapStruct Mapper für die Konvertierung zwischen Item-Entitäten und ItemDTOs/ItemDisplayDTOs.
 * Dieser Mapper ist als Spring-Bean konfiguriert.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    // --- Mappings für ItemDTO (für API-Eingabe und reine Datenübertragung) ---

    @Mapping(target = "addedBy", source = "addedByUserId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "shoppingList", source = "shoppingListId", qualifiedByName = "mapShoppingListIdToShoppingList")
    Item itemDTOToItem(ItemDTO itemDTO, @Context UserRepo userRepo, @Context ShoppingListRepo shoppingListRepo);

    @Mapping(target = "addedByUserId", source = "addedBy.id")
    @Mapping(target = "shoppingListId", source = "shoppingList.id")
    ItemDTO itemToItemDTO(Item item);

    List<ItemDTO> itemsToItemDTOs(List<Item> items);

    List<Item> itemDTOsToItems(List<ItemDTO> itemDTOs, @Context UserRepo userRepo, @Context ShoppingListRepo shoppingListRepo);


    // --- NEU: Mappings für ItemDisplayDTO (für UI-Anzeige) ---

    /**
     * Konvertiert eine Item-Entität in ein ItemDisplayDTO.
     * Löst die Namen der zugehörigen User- und ShoppingList-Entitäten auf.
     *
     * @param item Die zu konvertierende Item-Entität.
     * @return Das konvertierte ItemDisplayDTO.
     */
    @Mapping(target = "addedByUserId", source = "addedBy.id")
    @Mapping(target = "addedByUserName", source = "addedBy.username") // Mappe den Benutzernamen
    @Mapping(target = "shoppingListId", source = "shoppingList.id")
    @Mapping(target = "shoppingListName", source = "shoppingList.name") // Mappe den Namen der Einkaufsliste
    ItemDisplayDTO itemToItemDisplayDTO(Item item);

    /**
     * Konvertiert eine Liste von Item-Entitäten in eine Liste von ItemDisplayDTOs.
     *
     * @param items Die Liste der zu konvertierenden Item-Entitäten.
     * @return Die Liste der konvertierten ItemDisplayDTOs.
     */
    List<ItemDisplayDTO> itemsToItemDisplayDTOs(List<Item> items);


    // --- Hilfsmethoden für das Mapping (bereits vorhanden) ---

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId, @Context UserRepo userRepo) {
        return Optional.ofNullable(userId)
                .flatMap(userRepo::findById)
                .orElse(null);
    }

    @Named("mapShoppingListIdToShoppingList")
    default ShoppingList mapShoppingListIdToShoppingList(Long shoppingListId, @Context ShoppingListRepo shoppingListRepo) {
        return Optional.ofNullable(shoppingListId)
                .flatMap(shoppingListRepo::findById)
                .orElse(null);
    }
}
