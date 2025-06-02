package com.foodlist.service.mapper;

import com.foodlist.service.dto.UserDTO;
import com.foodlist.service.model.Household;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.HouseholdRepo;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

/**
 * MapStruct Mapper für die Konvertierung zwischen User-Entitäten und UserDTOs.
 * Dieser Mapper ist als Spring-Bean konfiguriert.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Konvertiert ein UserDTO in eine User-Entität.
     * Löst die zugehörige Household-Entität anhand ihrer ID auf.
     * Das 'passwordHash' Feld wird ignoriert, da es nicht direkt vom DTO kommen sollte.
     * Die 'itemsAdded' Liste wird ebenfalls ignoriert, da sie eine bidirektionale Beziehung ist,
     * die vom Item-Model verwaltet wird.
     *
     * @param userDTO Das zu konvertierende UserDTO.
     * @param householdRepo Das HouseholdRepo zur Auflösung der Household-Entität.
     * @return Die konvertierte User-Entität.
     */
    @Mapping(target = "household", source = "householdId", qualifiedByName = "mapHouseholdIdToHousehold")
    @Mapping(target = "passwordHash", ignore = true) // Passwort-Hash sollte nicht direkt vom DTO kommen
    @Mapping(target = "itemsAdded", ignore = true) // Items werden nicht über den UserDTO gesetzt
    User userDTOToUser(UserDTO userDTO, @Context HouseholdRepo householdRepo);

    /**
     * Konvertiert eine User-Entität in ein UserDTO.
     * Extrahiert die ID der zugehörigen Household-Entität.
     * Das 'passwordHash' Feld wird ignoriert, da es nicht an den Client gesendet werden sollte.
     * Die 'itemsAdded' Liste wird ebenfalls ignoriert, um Zyklen und große Payloads zu vermeiden.
     *
     * @param user Die zu konvertierende User-Entität.
     * @return Das konvertierte UserDTO.
     */
    @Mapping(target = "householdId", source = "household.id")

    UserDTO userToUserDTO(User user);

    /**
     * Konvertiert eine Liste von User-Entitäten in eine Liste von UserDTOs.
     *
     * @param users Die Liste der zu konvertierenden User-Entitäten.
     * @return Die Liste der konvertierten UserDTOs.
     */
    List<UserDTO> usersToUserDTOs(List<User> users);

    /**
     * Konvertiert eine Liste von UserDTOs in eine Liste von User-Entitäten.
     *
     * @param userDTOs Die Liste der zu konvertierenden UserDTOs.
     * @param householdRepo Das HouseholdRepo zur Auflösung der Household-Entitäten.
     * @return Die Liste der konvertierten User-Entitäten.
     */
    List<User> userDTOsToUsers(List<UserDTO> userDTOs, @Context HouseholdRepo householdRepo);

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
