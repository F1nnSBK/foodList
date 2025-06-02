package com.foodlist.service.controller;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO;
import com.foodlist.service.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
// Import für MethodArgumentNotValidException, falls Sie eine globale Fehlerbehandlung haben
// import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
// NoSuchElementException wird nicht mehr direkt im Service geworfen, daher hier entfernt
// import java.util.NoSuchElementException;

/**
 * REST Controller for managing Item resources.
 * Handles HTTP requests related to items, including CRUD operations.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/items") // Base path for all item-related endpoints
public class ItemController {

    private final ItemService itemService;

    /**
     * Constructor for ItemController, injecting the ItemService dependency.
     * Spring will automatically inject this bean.
     *
     * @param itemService The service responsible for item-related business logic.
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Retrieves all items.
     * Maps to GET /api/v1/items
     *
     * @return A list of all ItemDTOs.
     */
    @GetMapping // Simplified mapping, equivalent to @GetMapping("/")
    public List<ItemDisplayDTO> getAllItems() { // Renamed for better conciseness
        return itemService.getAllItems();
    }

    /**
     * Retrieves a single item by its ID.
     * Maps to GET /api/v1/items/{itemId}
     *
     * @param itemId The unique ID of the item to retrieve.
     * @return The ItemDTO of the found item.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the item does not exist.
     */
    @GetMapping("/{itemId}")
    public ItemDisplayDTO getItemById(@PathVariable Long itemId) {
        try {
            return itemService.getItemById(itemId);
        } catch (EntityNotFoundException enfe) {
            // Log the exception for debugging purposes if needed
            // log.warn("Item with ID {} not found: {}", itemId, enfe.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item with ID " + itemId + " not found.");
        }
    }

    /**
     * Adds a new item.
     * Maps to POST /api/v1/items
     *
     * @param item The ItemDTO containing the details of the item to add.
     * The @Valid annotation triggers bean validation.
     * @return The ItemDTO of the newly created item.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if validation fails or
     * if there's a constraint violation (e.g., non-existent related entity ID).
     */
    @PostMapping // Simplified mapping, equivalent to @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status to 201 Created on success
    public ItemDTO addItem(@Valid @RequestBody ItemDTO item) {
        try {
            return this.itemService.addItem(item);
        } catch (EntityNotFoundException enfe) {
            // This catches cases where addedByUserId or shoppingListId do not exist
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, enfe.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions.
            // For validation errors (@Valid), Spring typically throws MethodArgumentNotValidException,
            // which is often handled by a global @ControllerAdvice for cleaner responses.
            // If ConstraintViolationException is still thrown by the service, it's caught here.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add item: " + e.getMessage());
        }
    }

    /**
     * Updates an existing item.
     * Maps to PUT /api/v1/items/{itemId}
     *
     * @param id The ID of the item to update, extracted from the path.
     * @param item The ItemDTO containing the updated details.
     * The ID in the DTO is set from the path variable for consistency.
     * @return The ItemDTO of the updated item.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the item to update does not exist.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if related entities (User, ShoppingList) are not found
     * or other data integrity issues occur.
     */
    @PutMapping("/{itemId}")
    public ItemDTO updateItem(@PathVariable("itemId") Long id,
                              @Valid @RequestBody ItemDTO item) {
        // Ensure the ID from the path matches the ID in the request body for consistency
        // or set the DTO's ID from the path if the DTO's ID is not expected in the body.
        item.setId(id);
        try {
            return this.itemService.updateItem(item);
        } catch (EntityNotFoundException e) {
            // Catches if the item itself or a related entity (User/ShoppingList) is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions, e.g., validation or other constraint violations
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update item: " + e.getMessage());
        }
    }

    /**
     * Deletes an item by its ID.
     * Maps to DELETE /api/v1/items/{itemId}
     *
     * @param id The ID of the item to delete.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the item does not exist.
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Sets the HTTP status to 204 No Content on successful deletion
    public void deleteItemById(@PathVariable("itemId") Long id) {
        try {
            this.itemService.deleteItemById(id);
        } catch (EntityNotFoundException e) {
            // Catches if the item to delete is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item with ID " + id + " not found for deletion.");
        }
    }
}
