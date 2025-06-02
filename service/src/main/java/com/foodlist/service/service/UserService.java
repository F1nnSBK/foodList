package com.foodlist.service.service;

import com.foodlist.service.dto.UserDTO;
import java.util.List;

/**
 * Interface für den User Service, das den Vertrag für Operationen im Zusammenhang mit Benutzern definiert.
 */
public interface UserService {

    /**
     * Fügt einen neuen Benutzer zum System hinzu.
     *
     * @param userDTO Das UserDTO, das die Details des hinzuzufügenden Benutzers enthält.
     * Beachten Sie, dass für die Erstellung eines Benutzers in der Regel ein Passwort
     * (oder ein Hash davon) benötigt wird, das in diesem DTO möglicherweise nicht enthalten ist.
     * In einer realen Anwendung würde hier oft ein spezifischeres DTO für die Benutzererstellung verwendet.
     * @return Das UserDTO des neu hinzugefügten Benutzers, einschließlich seiner generierten ID.
     */
    UserDTO addUser(UserDTO userDTO);

    /**
     * Ruft alle Benutzer aus dem System ab.
     *
     * @return Eine Liste von UserDTOs, die alle Benutzer repräsentieren.
     */
    List<UserDTO> getAllUsers();

    /**
     * Ruft einen Benutzer anhand seiner eindeutigen ID ab.
     *
     * @param id Die ID des abzurufenden Benutzers.
     * @return Das UserDTO, das der gegebenen ID entspricht.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Benutzer mit der gegebenen ID gefunden wird.
     */
    UserDTO getUserById(Long id);

    /**
     * Aktualisiert einen bestehenden Benutzer.
     *
     * @param userDTO Das UserDTO, das die aktualisierten Details des Benutzers enthält.
     * Die ID im DTO muss einem bestehenden Benutzer entsprechen.
     * @return Das UserDTO des aktualisierten Benutzers.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Benutzer mit der ID im DTO gefunden wird.
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * Löscht einen Benutzer anhand seiner eindeutigen ID.
     *
     * @param id Die ID des zu löschenden Benutzers.
     * @throws jakarta.persistence.EntityNotFoundException wenn kein Benutzer mit der gegebenen ID gefunden wird.
     */
    void deleteUserById(Long id);
}
