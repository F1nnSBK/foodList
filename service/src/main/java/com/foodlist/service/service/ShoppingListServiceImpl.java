package com.foodlist.service.service;

import com.foodlist.service.dto.ShoppingListDTO;
import com.foodlist.service.mapper.ShoppingListMapper;
import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.repository.HouseholdRepo; // Import HouseholdRepo
import com.foodlist.service.repository.ShoppingListRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j // Lombok Annotation für Logging
@Service // Markiert diese Klasse als Spring Service Komponente
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListRepo shoppingListRepo;
    private final HouseholdRepo householdRepo; // Abhängigkeit für Household Entität

    /**
     * Konstruktor für ShoppingListServiceImpl, der die erforderlichen Abhängigkeiten injiziert.
     * Spring wird diese Beans automatisch injizieren.
     *
     * @param shoppingListMapper Der Mapper für die Konvertierung zwischen ShoppingList und ShoppingListDTO.
     * @param shoppingListRepo Das Repository für ShoppingList Entitäten.
     * @param householdRepo Das Repository für Household Entitäten, benötigt zur Auflösung von householdId.
     */
    public ShoppingListServiceImpl(ShoppingListMapper shoppingListMapper,
                                   ShoppingListRepo shoppingListRepo,
                                   HouseholdRepo householdRepo) {
        this.shoppingListMapper = shoppingListMapper;
        this.shoppingListRepo = shoppingListRepo;
        this.householdRepo = householdRepo;
    }

    /**
     * Fügt eine neue Einkaufsliste basierend auf dem bereitgestellten ShoppingListDTO hinzu.
     * Diese Methode behandelt die Konvertierung von DTO zu Entität,
     * löst die zugehörige Household Entität anhand ihrer ID auf,
     * setzt den Erstellungszeitstempel und speichert die Einkaufsliste in der Datenbank.
     *
     * @param shoppingListDTO Das DTO, das Details der Einkaufsliste enthält, einschließlich householdId.
     * @return Das DTO der neu erstellten Einkaufsliste, einschließlich ihrer generierten ID und des Zeitstempels.
     * @throws EntityNotFoundException wenn der angegebene Haushalt nicht existiert.
     * @throws Exception für andere unerwartete Fehler während des Prozesses.
     */
    @Override
    public ShoppingListDTO addShoppingList(ShoppingListDTO shoppingListDTO) {
        try {
            // Konvertiere ShoppingListDTO zu ShoppingList Entität mit dem Mapper
            ShoppingList shoppingList = shoppingListMapper.shoppingListDTOToShoppingList(shoppingListDTO, householdRepo);

            // Setze den Erstellungszeitstempel für die neue Einkaufsliste
            shoppingList.setCreatedAt(LocalDateTime.now());

            // Löse die Household Entität basierend auf householdId aus dem DTO auf und setze sie
            if (shoppingListDTO.getHouseholdId() != null) {
                Household household = householdRepo.findById(shoppingListDTO.getHouseholdId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Household mit ID " + shoppingListDTO.getHouseholdId() + " nicht gefunden. Kann Einkaufsliste nicht hinzufügen."));
                shoppingList.setHousehold(household);
            } else {
                // Wenn householdId null ist, entscheiden Sie über die Geschäftslogik:
                // - Setze household auf null, wenn es optional ist.
                // - Werfe eine IllegalArgumentException, wenn es obligatorisch ist.
                // Für dieses Beispiel setzen wir es auf null.
                shoppingList.setHousehold(null);
            }

            // Speichere die vorbereitete ShoppingList Entität in der Datenbank
            ShoppingList savedShoppingList = shoppingListRepo.save(shoppingList);
            // Konvertiere die gespeicherte ShoppingList Entität zurück zu DTO und gib sie zurück
            return shoppingListMapper.shoppingListToShoppingListDTO(savedShoppingList);
        } catch (Exception e) {
            // Protokolliere den Fehler mit vollständigem Stack-Trace für besseres Debugging
            log.error("Fehler beim Hinzufügen der Einkaufsliste: {}", e.getMessage(), e);
            // Werfe die Ausnahme erneut, damit sie von höheren Schichten (z.B. ControllerAdvice) behandelt wird
            throw e;
        }
    }

    /**
     * Ruft alle Einkaufslisten ab und konvertiert sie in ShoppingListDTOs.
     *
     * @return Eine Liste von ShoppingListDTOs.
     */
    @Override
    public List<ShoppingListDTO> getAllShoppingLists() {
        return shoppingListMapper.shoppingListsToShoppingListDTOs(shoppingListRepo.findAll());
    }

    /**
     * Ruft eine Einkaufsliste anhand ihrer ID ab und konvertiert sie in ShoppingListDTO.
     * Verwendet Optional.orElseThrow für eine robuste Fehlerbehandlung, wenn die Einkaufsliste nicht gefunden wird.
     *
     * @param id Die ID der abzurufenden Einkaufsliste.
     * @return Das ShoppingListDTO der gefundenen Einkaufsliste.
     * @throws EntityNotFoundException wenn keine Einkaufsliste mit der gegebenen ID existiert.
     */
    @Override
    public ShoppingListDTO getShoppingListById(Long id) {
        ShoppingList shoppingList = shoppingListRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Einkaufsliste mit ID " + id + " nicht gefunden."));
        return shoppingListMapper.shoppingListToShoppingListDTO(shoppingList);
    }

    /**
     * Aktualisiert eine bestehende Einkaufsliste basierend auf dem bereitgestellten ShoppingListDTO.
     * Es ruft die bestehende Einkaufsliste ab, aktualisiert ihre Felder einschließlich Beziehungen,
     * und speichert die Änderungen.
     *
     * @param shoppingListDTO Das DTO mit aktualisierten Details der Einkaufsliste. Die ID muss einer bestehenden Einkaufsliste entsprechen.
     * @return Das DTO der aktualisierten Einkaufsliste.
     * @throws EntityNotFoundException wenn die zu aktualisierende Einkaufsliste oder der zugehörige Haushalt nicht existiert.
     */
    @Override
    public ShoppingListDTO updateShoppingList(ShoppingListDTO shoppingListDTO) {
        // Stelle sicher, dass die Einkaufsliste existiert, bevor ein Update versucht wird
        ShoppingList existingShoppingList = shoppingListRepo.findById(shoppingListDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Zu aktualisierende Einkaufsliste nicht gefunden. Einkaufslisten-ID: " + shoppingListDTO.getId()));

        // Aktualisiere grundlegende Felder vom DTO zur bestehenden Entität
        existingShoppingList.setName(shoppingListDTO.getName());
        existingShoppingList.setDefault(shoppingListDTO.isDefault());
        // Aktualisiere den Zeitstempel beim Update, gemäß der ursprünglichen Logik. Überlegen Sie, ob dies das gewünschte Verhalten ist.
        existingShoppingList.setCreatedAt(LocalDateTime.now());

        // Behandle die Household-Beziehungsaktualisierung
        if (shoppingListDTO.getHouseholdId() != null) {
            Household household = householdRepo.findById(shoppingListDTO.getHouseholdId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Household mit ID " + shoppingListDTO.getHouseholdId() + " nicht gefunden für Update."));
            existingShoppingList.setHousehold(household);
        } else {
            // Wenn householdId im DTO null ist, setze die Beziehung in der Entität auf null.
            existingShoppingList.setHousehold(null);
        }

        // Speichere die aktualisierte bestehende ShoppingList Entität
        ShoppingList updatedShoppingList = shoppingListRepo.save(existingShoppingList);
        return shoppingListMapper.shoppingListToShoppingListDTO(updatedShoppingList);
    }

    /**
     * Löscht eine Einkaufsliste anhand ihrer ID.
     * Prüft auf Existenz vor dem Löschen, um eine spezifischere Fehlermeldung zu liefern, falls nicht gefunden.
     *
     * @param id Die ID der zu löschenden Einkaufsliste.
     * @throws EntityNotFoundException wenn keine Einkaufsliste mit der gegebenen ID existiert.
     */
    @Override
    public void deleteShoppingListById(Long id) {
        if (!shoppingListRepo.existsById(id)) {
            throw new EntityNotFoundException("Einkaufsliste mit ID " + id + " nicht gefunden zum Löschen.");
        }
        shoppingListRepo.deleteById(id);
    }
}
