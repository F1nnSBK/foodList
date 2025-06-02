package com.foodlist.service.controller;

import com.foodlist.service.dto.HouseholdDTO;
import com.foodlist.service.service.HouseholdService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST Controller for managing Household resources.
 * Handles HTTP requests related to households, including CRUD operations.
 */
@RestController // Marks this class as a REST controller
@RequestMapping("/api/v1/households") // Base path for all household-related endpoints
public class HouseholdController {

    private final HouseholdService householdService;

    /**
     * Constructor for HouseholdController, injecting the HouseholdService dependency.
     * Spring will automatically inject this bean.
     *
     * @param householdService The service responsible for household-related business logic.
     */
    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    /**
     * Retrieves all households.
     * Maps to GET /api/v1/households
     *
     * @return A list of all HouseholdDTOs.
     */
    @GetMapping
    public List<HouseholdDTO> getAllHouseholds() {
        return householdService.getAllHouseholds();
    }

    /**
     * Retrieves a single household by its ID.
     * Maps to GET /api/v1/households/{householdId}
     *
     * @param householdId The unique ID of the household to retrieve.
     * @return The HouseholdDTO of the found household.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the household does not exist.
     */
    @GetMapping("/{householdId}")
    public HouseholdDTO getHouseholdById(@PathVariable Long householdId) {
        try {
            return householdService.getHouseholdById(householdId);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Household with ID " + householdId + " not found.");
        }
    }

    /**
     * Adds a new household.
     * Maps to POST /api/v1/households
     *
     * @param household The HouseholdDTO containing the details of the household to add.
     * The @Valid annotation triggers bean validation.
     * @return The HouseholdDTO of the newly created household.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if validation fails or
     * if there's a constraint violation (e.g., non-existent related entity ID like userIds or shoppingListIds).
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Sets the HTTP status to 201 Created on success
    public HouseholdDTO addHousehold(@Valid @RequestBody HouseholdDTO household) {
        try {
            return this.householdService.addHousehold(household);
        } catch (EntityNotFoundException enfe) {
            // This catches cases where userIds or shoppingListIds do not exist
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, enfe.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to add household: " + e.getMessage());
        }
    }

    /**
     * Updates an existing household.
     * Maps to PUT /api/v1/households/{householdId}
     *
     * @param id The ID of the household to update, extracted from the path.
     * @param household The HouseholdDTO containing the updated details.
     * The ID in the DTO is set from the path variable for consistency.
     * @return The HouseholdDTO of the updated household.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the household to update does not exist.
     * @throws ResponseStatusException with HttpStatus.BAD_REQUEST if related entities (Users, ShoppingLists) are not found
     * or other data integrity issues occur.
     */
    @PutMapping("/{householdId}")
    public HouseholdDTO updateHousehold(@PathVariable("householdId") Long id,
                                        @Valid @RequestBody HouseholdDTO household) {
        // Ensure the ID from the path matches the ID in the request body for consistency
        household.setId(id);
        try {
            return this.householdService.updateHousehold(household);
        } catch (EntityNotFoundException e) {
            // Catches if the household itself or a related entity (User/ShoppingList) is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected exceptions, e.g., validation or other constraint violations
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update household: " + e.getMessage());
        }
    }

    /**
     * Deletes a household by its ID.
     * Maps to DELETE /api/v1/households/{householdId}
     *
     * @param id The ID of the household to delete.
     * @throws ResponseStatusException with HttpStatus.NOT_FOUND if the household does not exist.
     */
    @DeleteMapping("/{householdId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Sets the HTTP status to 204 No Content on successful deletion
    public void deleteHouseholdById(@PathVariable("householdId") Long id) {
        try {
            this.householdService.deleteHouseholdById(id);
        } catch (EntityNotFoundException e) {
            // Catches if the household to delete is not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Household with ID " + id + " not found for deletion.");
        }
    }
}
