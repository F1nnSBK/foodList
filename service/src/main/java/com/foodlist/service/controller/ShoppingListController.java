package com.foodlist.service.controller;

import com.foodlist.service.dto.ShoppingListDTO;
import com.foodlist.service.service.ShoppingListService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST Controller for managing ShoppingList resources.
 * Handles HTTP requests related to shopping lists, including CRUD operations.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/shoppinglists") // Base path for all shopping list-related endpoints
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    /**
     * Constructor for ShoppingListController, injecting the ShoppingListService dependency.
     * Spring will automatically inject this bean.
     *
     * @param shoppingListService The service responsible for shopping list-related business logic.
     */
    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    /**
     * Retrieves all shopping lists.
     * Maps to GET /api/v1/shoppinglists
     *
     * @return A list of all ShoppingListDTOs.
     */
    @GetMapping
    public List<ShoppingListDTO> getAllShoppingLists() {
        return shoppingListService.getAllShoppingLists();
    }

    /**
     * Retrieves a single shopping list by its ID.
     * Maps to GET /api/v1/shoppinglists/{shoppingListId}
     *
     * @param shoppingListId The unique ID of the shopping list to retrieve.
     * @return The ShoppingListDTO of the found shopping list.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the shopping list does not exist.
     */
    @GetMapping("/{shoppingListId}")
    public ShoppingListDTO getShoppingListById(@PathVariable Long shoppingListId) {
        try {
            return shoppingListService.getShoppingListById(shoppingListId);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ShoppingList with ID " + shoppingListId + " not found.");
        }
    }

    /**
     * Adds a new shopping list.
     * Maps to POST /api/v1/shoppinglists
     *
     * @param shoppingList The ShoppingListDTO containing the details of the shopping list to add.
     * The @Valid annotation triggers bean validation.
     * @return The ShoppingListDTO of the newly created shopping list.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if validation fails or
     * if there's a constraint violation (e.g., non-existent related entity ID like householdId).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status to 201 Created on success
    public ShoppingListDTO addShoppingList(@Valid @RequestBody ShoppingListDTO shoppingList) {
        try {
            return this.shoppingListService.addShoppingList(shoppingList);
        } catch (EntityNotFoundException enfe) {
            // This catches cases where householdId does not exist
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, enfe.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add shopping list: " + e.getMessage());
        }
    }

    /**
     * Updates an existing shopping list.
     * Maps to PUT /api/v1/shoppinglists/{shoppingListId}
     *
     * @param id The ID of the shopping list to update, extracted from the path.
     * @param shoppingList The ShoppingListDTO containing the updated details.
     * The ID in the DTO is set from the path variable for consistency.
     * @return The ShoppingListDTO of the updated shopping list.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the shopping list to update does not exist.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if related entities (Household) are not found
     * or other data integrity issues occur.
     */
    @PutMapping("/{shoppingListId}")
    public ShoppingListDTO updateShoppingList(@PathVariable("shoppingListId") Long id,
                                              @Valid @RequestBody ShoppingListDTO shoppingList) {
        // Ensure the ID from the path matches the ID in the request body for consistency
        shoppingList.setId(id);
        try {
            return this.shoppingListService.updateShoppingList(shoppingList);
        } catch (EntityNotFoundException e) {
            // Catches if the shopping list itself or a related entity (Household) is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions, e.g., validation or other constraint violations
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update shopping list: " + e.getMessage());
        }
    }

    /**
     * Deletes a shopping list by its ID.
     * Maps to DELETE /api/v1/shoppinglists/{shoppingListId}
     *
     * @param id The ID of the shopping list to delete.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the shopping list does not exist.
     */
    @DeleteMapping("/{shoppingListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Sets the HTTP status to 204 No Content on successful deletion
    public void deleteShoppingListById(@PathVariable("shoppingListId") Long id) {
        try {
            this.shoppingListService.deleteShoppingListById(id);
        } catch (EntityNotFoundException e) {
            // Catches if the shopping list to delete is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ShoppingList with ID " + id + " not found for deletion.");
        }
    }
}
