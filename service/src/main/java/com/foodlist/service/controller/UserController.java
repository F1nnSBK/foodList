package com.foodlist.service.controller;

import com.foodlist.service.dto.UserDTO;
import com.foodlist.service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST Controller for managing User resources.
 * Handles HTTP requests related to users, including CRUD operations.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/users") // Base path for all user-related endpoints
public class UserController {

    private final UserService userService;

    /**
     * Constructor for UserController, injecting the UserService dependency.
     * Spring will automatically inject this bean.
     *
     * @param userService The service responsible for user-related business logic.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users.
     * Maps to GET /api/v1/users
     *
     * @return A list of all UserDTOs.
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a single user by their ID.
     * Maps to GET /api/v1/users/{userId}
     *
     * @param userId The unique ID of the user to retrieve.
     * @return The UserDTO of the found user.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the user does not exist.
     */
    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        try {
            return userService.getUserById(userId);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }
    }

    /**
     * Adds a new user.
     * Maps to POST /api/v1/users
     *
     * @param user The UserDTO containing the details of the user to add.
     * The @Valid annotation triggers bean validation.
     * @return The UserDTO of the newly created user.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if validation fails or
     * if there's a constraint violation (e.g., non-existent related entity ID like householdId, or duplicate username/email).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status to 201 Created on success
    public UserDTO addUser(@Valid @RequestBody UserDTO user) {
        try {
            return this.userService.addUser(user);
        } catch (EntityNotFoundException enfe) {
            // This catches cases where householdId does not exist
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, enfe.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions, e.g., validation or unique constraint violations
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add user: " + e.getMessage());
        }
    }

    /**
     * Updates an existing user.
     * Maps to PUT /api/v1/users/{userId}
     *
     * @param id The ID of the user to update, extracted from the path.
     * @param user The UserDTO containing the updated details.
     * The ID in the DTO is set from the path variable for consistency.
     * @return The UserDTO of the updated user.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the user to update does not exist.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if related entities (Household) are not found
     * or other data integrity issues occur (e.g., duplicate username/email).
     */
    @PutMapping("/{userId}")
    public UserDTO updateUser(@PathVariable("userId") Long id,
                              @Valid @RequestBody UserDTO user) {
        // Ensure the ID from the path matches the ID in the request body for consistency
        user.setId(id);
        try {
            return this.userService.updateUser(user);
        } catch (EntityNotFoundException e) {
            // Catches if the user itself or a related entity (Household) is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions, e.g., validation or other constraint violations
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update user: " + e.getMessage());
        }
    }

    /**
     * Deletes a user by their ID.
     * Maps to DELETE /api/v1/users/{userId}
     *
     * @param id The ID of the user to delete.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the user does not exist.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Sets the HTTP status to 204 No Content on successful deletion
    public void deleteUserById(@PathVariable("userId") Long id) {
        try {
            this.userService.deleteUserById(id);
        } catch (EntityNotFoundException e) {
            // Catches if the user to delete is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + id + " not found for deletion.");
        }
    }
}
