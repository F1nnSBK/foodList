package com.foodlist.service.mapper;

import com.foodlist.service.dto.HouseholdDTO;
import com.foodlist.service.model.Household;
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
import java.util.stream.Collectors;

/**
 * MapStruct Mapper für die Konvertierung zwischen Household-Entitäten und HouseholdDTOs.
 * Dieser Mapper ist als Spring-Bean konfiguriert.
 */
@Mapper(componentModel = "spring")
public interface HouseholdMapper {

    /**
     * Konvertiert ein HouseholdDTO in eine Household-Entität.
     * Löst die zugehörigen User- und ShoppingList-Entitäten anhand ihrer IDs auf.
     *
     * @param householdDTO Das zu konvertierende HouseholdDTO.
     * @param userRepo Das UserRepo zur Auflösung der User-Entitäten.
     * @param shoppingListRepo Das ShoppingListRepo zur Auflösung der ShoppingList-Entitäten.
     * @return Die konvertierte Household-Entität.
     */
    @Mapping(target = "users", source = "userIds", qualifiedByName = "mapUserIdsToUsers")
    @Mapping(target = "shoppingLists", source = "shoppingListIds", qualifiedByName = "mapShoppingListIdsToShoppingLists")
    Household householdDTOToHousehold(HouseholdDTO householdDTO, @Context UserRepo userRepo, @Context ShoppingListRepo shoppingListRepo);

    /**
     * Konvertiert eine Household-Entität in ein HouseholdDTO.
     * Extrahiert die IDs der zugehörigen User- und ShoppingList-Entitäten.
     *
     * @param household Die zu konvertierende Household-Entität.
     * @return Das konvertierte HouseholdDTO.
     */
    @Mapping(target = "userIds", source = "users", qualifiedByName = "mapUsersToUserIds")
    @Mapping(target = "shoppingListIds", source = "shoppingLists", qualifiedByName = "mapShoppingListsToShoppingListIds")
    HouseholdDTO householdToHouseholdDTO(Household household);

    /**
     * Konvertiert eine Liste von Household-Entitäten in eine Liste von HouseholdDTOs.
     *
     * @param households Die Liste der zu konvertierenden Household-Entitäten.
     * @return Die Liste der konvertierten HouseholdDTOs.
     */
    List<HouseholdDTO> householdsToHouseholdDTOs(List<Household> households);

    /**
     * Konvertiert eine Liste von HouseholdDTOs in eine Liste von Household-Entitäten.
     *
     * @param householdDTOs Die Liste der zu konvertierenden HouseholdDTOs.
     * @param userRepo Das UserRepo zur Auflösung der User-Entitäten.
     * @param shoppingListRepo Das ShoppingListRepo zur Auflösung der ShoppingList-Entitäten.
     * @return Die Liste der konvertierten Household-Entitäten.
     */
    List<Household> householdDTOsToHouseholds(List<HouseholdDTO> householdDTOs, @Context UserRepo userRepo, @Context ShoppingListRepo shoppingListRepo);

    /**
     * Hilfsmethode zum Mappen einer Liste von Benutzer-IDs zu einer Liste von User-Entitäten.
     * Verwendet von MapStruct über @Named.
     *
     * @param userIds Die Liste der Benutzer-IDs.
     * @param userRepo Das UserRepo zur Suche der Benutzer.
     * @return Die Liste der User-Entitäten.
     */
    @Named("mapUserIdsToUsers")
    default List<User> mapUserIdsToUsers(List<Long> userIds, @Context UserRepo userRepo) {
        if (userIds == null) {
            return null;
        }
        return userIds.stream()
                .map(id -> userRepo.findById(id).orElse(null)) // Oder werfen Sie eine EntityNotFoundException
                .filter(java.util.Objects::nonNull) // Filtert null-Werte heraus, wenn eine ID nicht gefunden wurde
                .collect(Collectors.toList());
    }

    /**
     * Hilfsmethode zum Mappen einer Liste von ShoppingList-IDs zu einer Liste von ShoppingList-Entitäten.
     * Verwendet von MapStruct über @Named.
     *
     * @param shoppingListIds Die Liste der ShoppingList-IDs.
     * @param shoppingListRepo Das ShoppingListRepo zur Suche der Einkaufslisten.
     * @return Die Liste der ShoppingList-Entitäten.
     */
    @Named("mapShoppingListIdsToShoppingLists")
    default List<ShoppingList> mapShoppingListIdsToShoppingLists(List<Long> shoppingListIds, @Context ShoppingListRepo shoppingListRepo) {
        if (shoppingListIds == null) {
            return null;
        }
        return shoppingListIds.stream()
                .map(id -> shoppingListRepo.findById(id).orElse(null)) // Oder werfen Sie eine EntityNotFoundException
                .filter(java.util.Objects::nonNull) // Filtert null-Werte heraus, wenn eine ID nicht gefunden wurde
                .collect(Collectors.toList());
    }

    /**
     * Hilfsmethode zum Mappen einer Liste von User-Entitäten zu einer Liste von Benutzer-IDs.
     * Verwendet von MapStruct über @Named.
     *
     * @param users Die Liste der User-Entitäten.
     * @return Die Liste der Benutzer-IDs.
     */
    @Named("mapUsersToUserIds")
    default List<Long> mapUsersToUserIds(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    /**
     * Hilfsmethode zum Mappen einer Liste von ShoppingList-Entitäten zu einer Liste von ShoppingList-IDs.
     * Verwendet von MapStruct über @Named.
     *
     * @param shoppingLists Die Liste der ShoppingList-Entitäten.
     * @return Die Liste der ShoppingList-IDs.
     */
    @Named("mapShoppingListsToShoppingListIds")
    default List<Long> mapShoppingListsToShoppingListIds(List<ShoppingList> shoppingLists) {
        if (shoppingLists == null) {
            return null;
        }
        return shoppingLists.stream()
                .map(ShoppingList::getId)
                .collect(Collectors.toList());
    }
}
