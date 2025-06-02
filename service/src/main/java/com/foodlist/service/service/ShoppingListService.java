package com.foodlist.service.service;

import com.foodlist.service.dto.ShoppingListDTO;
import java.util.List;

/**
 * Interface für den ShoppingList Service, das den Vertrag für Operationen im Zusammenhang mit Einkaufslisten definiert.
 */
public interface ShoppingListService {

    /**
     * Fügt eine neue Einkaufsliste zum System hinzu.
     *
     * @param shoppingListDTO Das ShoppingListDTO, das die Details der hinzuzufügenden Einkaufsliste enthält.
     * @return Das ShoppingListDTO der neu hinzugefügten Einkaufsliste, einschließlich ihrer generierten ID.
     */
    ShoppingListDTO addShoppingList(ShoppingListDTO shoppingListDTO);

    /**
     * Ruft alle Einkaufslisten aus dem System ab.
     *
     * @return Eine Liste von ShoppingListDTOs, die alle Einkaufslisten repräsentieren.
     */
    List<ShoppingListDTO> getAllShoppingLists();

    /**
     * Ruft eine Einkaufsliste anhand ihrer eindeutigen ID ab.
     *
     * @param id Die ID der abzurufenden Einkaufsliste.
     * @return Das ShoppingListDTO, das der gegebenen ID entspricht.
     * @throws jakarta.persistence.EntityNotFoundException wenn keine Einkaufsliste mit der gegebenen ID gefunden wird.
     */
    ShoppingListDTO getShoppingListById(Long id);

    /**
     * Aktualisiert eine bestehende Einkaufsliste.
     *
     * @param shoppingListDTO Das ShoppingListDTO, das die aktualisierten Details der Einkaufsliste enthält.
     * Die ID im DTO muss einer bestehenden Einkaufsliste entsprechen.
     * @return Das ShoppingListDTO der aktualisierten Einkaufsliste.
     * @throws jakarta.persistence.EntityNotFoundException wenn keine Einkaufsliste mit der ID im DTO gefunden wird.
     */
    ShoppingListDTO updateShoppingList(ShoppingListDTO shoppingListDTO);

    /**
     * Löscht eine Einkaufsliste anhand ihrer eindeutigen ID.
     *
     * @param id Die ID der zu löschenden Einkaufsliste.
     * @throws jakarta.persistence.EntityNotFoundException wenn keine Einkaufsliste mit der gegebenen ID gefunden wird.
     */
    void deleteShoppingListById(Long id);
}
