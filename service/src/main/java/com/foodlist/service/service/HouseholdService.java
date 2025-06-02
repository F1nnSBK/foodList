package com.foodlist.service.service;

import com.foodlist.service.dto.HouseholdDTO;
import java.util.List;

/**
 * Interface für den Household Service, das den Vertrag für Operationen im Zusammenhang mit Haushalten definiert.
 */
public interface HouseholdService {

    /**
     * Fügt einen neuen Haushalt zum System hinzu.
     *
     * @param householdDTO Das HouseholdDTO, das die Details des hinzuzufügenden Haushalts enthält.
     * @return Das HouseholdDTO des neu hinzugefügten Haushalts, einschließlich seiner generierten ID.
     */
    HouseholdDTO addHousehold(HouseholdDTO householdDTO);

    /**
     * Ruft alle Haushalte aus dem System ab.
     *
     * @return Eine Liste von HouseholdDTOs, die alle Haushalte repräsentieren.
     */
    List<HouseholdDTO> getAllHouseholds();

    /**
     * Ruft einen Haushalt anhand seiner eindeutigen ID ab.
     *
     * @param id Die ID des abzurufenden Haushalts.
     * @return Das HouseholdDTO, das der gegebenen ID entspricht.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Haushalt mit der gegebenen ID gefunden wird.
     */
    HouseholdDTO getHouseholdById(Long id);

    /**
     * Aktualisiert einen bestehenden Haushalt.
     *
     * @param householdDTO Das HouseholdDTO, das die aktualisierten Details des Haushalts enthält.
     * Die ID im DTO muss einem bestehenden Haushalt entsprechen.
     * @return Das HouseholdDTO des aktualisierten Haushalts.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Haushalt mit der ID im DTO gefunden wird.
     */
    HouseholdDTO updateHousehold(HouseholdDTO householdDTO);

    /**
     * Löscht einen Haushalt anhand seiner eindeutigen ID.
     *
     * @param id Die ID des zu löschenden Haushalts.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Haushalt mit der gegebenen ID gefunden wird.
     */
    void deleteHouseholdById(Long id);
}
