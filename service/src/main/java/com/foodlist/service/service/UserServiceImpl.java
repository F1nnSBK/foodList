package com.foodlist.service.service;

import com.foodlist.service.dto.UserDTO;
import com.foodlist.service.mapper.UserMapper;
import com.foodlist.service.model.Household;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.HouseholdRepo; // Import HouseholdRepo
import com.foodlist.service.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j // Lombok Annotation für Logging
@Service // Markiert diese Klasse als Spring Service Komponente
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final HouseholdRepo householdRepo; // Abhängigkeit für Household Entität

    /**
     * Konstruktor für UserServiceImpl, der die erforderlichen Abhängigkeiten injiziert.
     * Spring wird diese Beans automatisch injizieren.
     *
     * @param userRepo Das Repository für User Entitäten.
     * @param userMapper Der Mapper für die Konvertierung zwischen User und UserDTO.
     * @param householdRepo Das Repository für Household Entitäten, benötigt zur Auflösung von householdId.
     */
    public UserServiceImpl(UserRepo userRepo, UserMapper userMapper,
                           HouseholdRepo householdRepo) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.householdRepo = householdRepo;
    }

    /**
     * Fügt einen neuen Benutzer basierend auf dem bereitgestellten UserDTO hinzu.
     * Diese Methode behandelt die Konvertierung von DTO zu Entität,
     * löst die zugehörige Household Entität anhand ihrer ID auf,
     * setzt den Erstellungszeitstempel und speichert den Benutzer in der Datenbank.
     *
     * HINWEIS ZUM PASSWORT: Da das UserDTO, das wir definiert haben, kein 'passwordHash' Feld enthält,
     * wird in dieser Implementierung das 'passwordHash' Feld des User-Models nicht direkt vom DTO befüllt.
     * In einer realen Anwendung würden Sie hier:
     * 1. Ein separates DTO für die Benutzererstellung verwenden, das ein Klartext-Passwortfeld enthält.
     * 2. Dieses Klartext-Passwort mit einem 'PasswordEncoder' (z.B. BCryptPasswordEncoder) hashen.
     * 3. Den gehashten Wert im User-Model setzen, bevor es gespeichert wird.
     * Für dieses Beispiel wird angenommen, dass das 'passwordHash' bereits auf andere Weise (z.B. durch
     * eine vorgelagerte Authentifizierungsschicht oder einen speziellen Registrierungs-DTO) gehandhabt wird,
     * oder dass es sich um eine vereinfachte Darstellung handelt, bei der das Passwort nicht über dieses DTO gesetzt wird.
     *
     * @param userDTO Das DTO, das Details des Benutzers enthält, einschließlich householdId.
     * @return Das DTO des neu erstellten Benutzers, einschließlich seiner generierten ID und des Zeitstempels.
     * @throws EntityNotFoundException wenn der angegebene Haushalt nicht existiert.
     * @throws Exception für andere unerwartete Fehler während des Prozesses.
     */
    @Override
    public UserDTO addUser(UserDTO userDTO) {
        try {
            // Konvertiere UserDTO zu User Entität mit dem Mapper
            // Pass the necessary repository as context for household resolution
            User user = userMapper.userDTOToUser(userDTO, householdRepo);

            // Set the creation timestamp for the new user
            user.setCreatedAt(LocalDateTime.now());

            // HINWEIS: Hier wäre der Platz, um das Passwort zu hashen und im 'user' Objekt zu setzen.
            // Beispiel: user.setPasswordHash(passwordEncoder.encode(userDTO.getRawPassword()));
            // Da UserDTO kein Passwortfeld hat, wird dies hier ausgelassen.
            // Stellen Sie sicher, dass 'passwordHash' im User-Model korrekt gesetzt wird,
            // bevor der Benutzer gespeichert wird, falls dies für die Datenbank erforderlich ist (nullable=false).
            // Für dieses Beispiel setzen wir einen Platzhalter, wenn es nicht vom DTO kommt.
            if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
                // Dies ist ein Platzhalter. In einer realen Anwendung MUSS ein Passwort gesetzt werden.
                user.setPasswordHash("default_hashed_password_placeholder");
                log.warn("Password hash not provided for new user {}. Using a placeholder. This should be handled securely.", userDTO.getUsername());
            }

            // Löse die Household Entität basierend auf householdId aus dem DTO auf und setze sie
            // Der Mapper sollte dies bereits getan haben. Hier eine zusätzliche Prüfung.
            if (userDTO.getHouseholdId() != null && user.getHousehold() == null) {
                throw new EntityNotFoundException(
                        "Household mit ID " + userDTO.getHouseholdId() + " nicht gefunden. Kann Benutzer nicht hinzufügen.");
            } else if (userDTO.getHouseholdId() == null && user.getHousehold() != null) {
                user.setHousehold(null);
            }

            // Speichere die vorbereitete User Entität in der Datenbank
            User savedUser = userRepo.save(user);
            // Konvertiere die gespeicherte User Entität zurück zu DTO und gib sie zurück
            return userMapper.userToUserDTO(savedUser);
        } catch (Exception e) {
            // Protokolliere den Fehler mit vollständigem Stack-Trace für besseres Debugging
            log.error("Fehler beim Hinzufügen des Benutzers: {}", e.getMessage(), e);
            // Werfe die Ausnahme erneut, damit sie von höheren Schichten (z.B. ControllerAdvice) behandelt wird
            throw e;
        }
    }

    /**
     * Ruft alle Benutzer ab und konvertiert sie in UserDTOs.
     *
     * @return Eine Liste von UserDTOs.
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.usersToUserDTOs(userRepo.findAll());
    }

    /**
     * Ruft einen Benutzer anhand seiner ID ab und konvertiert ihn in UserDTO.
     * Verwendet Optional.orElseThrow für eine robuste Fehlerbehandlung, wenn der Benutzer nicht gefunden wird.
     *
     * @param id Die ID des abzurufenden Benutzers.
     * @return Das UserDTO des gefundenen Benutzers.
     * @throws EntityNotFoundException wenn kein Benutzer mit der gegebenen ID existiert.
     */
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benutzer mit ID " + id + " nicht gefunden."));
        return userMapper.userToUserDTO(user);
    }

    /**
     * Aktualisiert einen bestehenden Benutzer basierend auf dem bereitgestellten UserDTO.
     * Es ruft den bestehenden Benutzer ab, aktualisiert seine Felder einschließlich Beziehungen,
     * und speichert die Änderungen.
     *
     * HINWEIS ZUM PASSWORT: Diese Methode ist nicht dafür gedacht, das Passwort zu aktualisieren.
     * Passwort-Updates sollten über einen separaten, sicheren Endpunkt erfolgen.
     *
     * @param userDTO Das DTO mit aktualisierten Details des Benutzers. Die ID muss einem bestehenden Benutzer entsprechen.
     * @return Das DTO des aktualisierten Benutzers.
     * @throws EntityNotFoundException wenn der zu aktualisierende Benutzer oder der zugehörige Haushalt nicht existiert.
     */
    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        // Stelle sicher, dass der Benutzer existiert, bevor ein Update versucht wird
        User existingUser = userRepo.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Zu aktualisierender Benutzer nicht gefunden. Benutzer-ID: " + userDTO.getId()));

        // Aktualisiere grundlegende Felder vom DTO zur bestehenden Entität
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setName(userDTO.getName());
        existingUser.setEnabled(userDTO.isEnabled());
        // createdAt sollte normalerweise nicht aktualisiert werden
        // existingUser.setCreatedAt(LocalDateTime.now()); // Nur wenn dies Teil Ihrer Geschäftslogik ist

        // Behandle die Household-Beziehungsaktualisierung
        // Der Mapper sollte dies bereits tun, aber wir können hier eine explizite Prüfung machen.
        if (userDTO.getHouseholdId() != null) {
            Household household = householdRepo.findById(userDTO.getHouseholdId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Household mit ID " + userDTO.getHouseholdId() + " nicht gefunden für Update."));
            existingUser.setHousehold(household);
        } else {
            // Wenn householdId im DTO null ist, setze die Beziehung in der Entität auf null.
            existingUser.setHousehold(null);
        }

        // HINWEIS ZUM PASSWORT: Das Passwort wird hier nicht aktualisiert.
        // existingUser.setPasswordHash(userDTO.getPasswordHash()); // NICHT EMPFOHLEN für dieses DTO

        // Speichere die aktualisierte bestehende User Entität
        User updatedUser = userRepo.save(existingUser);
        return userMapper.userToUserDTO(updatedUser);
    }

    /**
     * Löscht einen Benutzer anhand seiner ID.
     * Prüft auf Existenz vor dem Löschen, um eine spezifischere Fehlermeldung zu liefern, falls nicht gefunden.
     *
     * @param id Die ID des zu löschenden Benutzers.
     * @throws EntityNotFoundException wenn kein Benutzer mit der gegebenen ID existiert.
     */
    @Override
    public void deleteUserById(Long id) {
        if (!userRepo.existsById(id)) {
            throw new EntityNotFoundException("Benutzer mit ID " + id + " nicht gefunden zum Löschen.");
        }
        userRepo.deleteById(id);
    }
}
