package com.foodlist.service.mapper;

import com.foodlist.service.dto.ShoppingListDTO;
import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.repository.HouseholdRepo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

/**
 * MapStruct Mapper für die Konvertierung zwischen ShoppingList-Entitäten und ShoppingListDTOs.
 * Dieser Mapper ist als Spring-Bean konfiguriert.
 */
@Mapper(componentModel = "spring")
public interface ShoppingListMapper {

    /**
     * Konvertiert ein ShoppingListDTO in eine ShoppingList-Entität.
     * Löst die zugehörige Household-Entität anhand ihrer ID auf.
     *
     * @param shoppingListDTO Das zu konvertierende ShoppingListDTO.
     * @param householdRepo Das HouseholdRepo zur Auflösung der Household-Entität.
     * @return Die konvertierte ShoppingList-Entität.
     */
    @Mapping(target = "household", source = "householdId", qualifiedByName = "mapHouseholdIdToHousehold")
    ShoppingList shoppingListDTOToShoppingList(ShoppingListDTO shoppingListDTO, @Context HouseholdRepo householdRepo);

    /**
     * Konvertiert eine ShoppingList-Entität in ein ShoppingListDTO.
     * Extrahiert die ID der zugehörigen Household-Entität.
     *
     * @param shoppingList Die zu konvertierende ShoppingList-Entität.
     * @return Das konvertierte ShoppingListDTO.
     */
    @Mapping(target = "householdId", source = "household.id")
    @Mapping(target = "items", ignore = true) // Ignoriere die Item-Liste im DTO, um Zyklen zu vermeiden.
    // Items sollten separat über ihren eigenen Endpunkt verwaltet werden.
    ShoppingListDTO shoppingListToShoppingListDTO(ShoppingList shoppingList);

    /**
     * Konvertiert eine Liste von ShoppingList-Entitäten in eine Liste von ShoppingListDTOs.
     *
     * @param shoppingLists Die Liste der zu konvertierenden ShoppingList-Entitäten.
     * @return Die Liste der konvertierten ShoppingListDTOs.
     */
    List<ShoppingListDTO> shoppingListsToShoppingListDTOs(List<ShoppingList> shoppingLists);

    /**
     * Konvertiert eine Liste von ShoppingListDTOs in eine Liste von ShoppingList-Entitäten.
     *
     * @param shoppingListDTOs Die Liste der zu konvertierenden ShoppingListDTOs.
     * @param householdRepo Das HouseholdRepo zur Auflösung der Household-Entitäten.
     * @return Die Liste der konvertierten ShoppingList-Entitäten.
     */
    List<ShoppingList> shoppingListDTOsToShoppingLists(List<ShoppingListDTO> shoppingListDTOs, @Context HouseholdRepo householdRepo);


    /**
     * Hilfsmethode zum Mappen einer Household-ID zu einer Household-Entität.
     * Verwendet von MapStruct über @Named.
     *
     * @param householdId Die ID des Haushalts.
     * @param householdRepo Das HouseholdRepo zur Suche des Haushalts.
     * @return Die Household-Entität oder null, wenn nicht gefunden.
     */
    @Named("mapHouseholdIdToHousehold")
    default Household mapHouseholdIdToHousehold(Long householdId, @Context HouseholdRepo householdRepo) {
        return Optional.ofNullable(householdId)
                .flatMap(householdRepo::findById)
                .orElse(null); // Oder werfen Sie eine EntityNotFoundException, je nach Geschäftslogik
    }
}
