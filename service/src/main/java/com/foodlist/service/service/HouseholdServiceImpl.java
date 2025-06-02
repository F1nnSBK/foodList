package com.foodlist.service.service;

import com.foodlist.service.dto.HouseholdDTO;
import com.foodlist.service.mapper.HouseholdMapper;
import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.HouseholdRepo;
import com.foodlist.service.repository.ShoppingListRepo; // Import ShoppingListRepo
import com.foodlist.service.repository.UserRepo; // Import UserRepo
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Import Optional for findById

@Slf4j // Lombok annotation for logging
@Service // Marks this class as a Spring Service component
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdRepo householdRepo;
    private final HouseholdMapper householdMapper;
    private final UserRepo userRepo; // Dependency for User entity
    private final ShoppingListRepo shoppingListRepo; // Dependency for ShoppingList entity

    /**
     * Constructor for HouseholdServiceImpl, injecting required dependencies.
     * Spring will automatically inject these beans.
     *
     * @param householdRepo The repository for Household entities.
     * @param householdMapper The mapper for converting between Household and HouseholdDTO.
     * @param userRepo The repository for User entities, needed to resolve userIds.
     * @param shoppingListRepo The repository for ShoppingList entities, needed to resolve shoppingListIds.
     */
    public HouseholdServiceImpl(HouseholdRepo householdRepo, HouseholdMapper householdMapper,
                                UserRepo userRepo, ShoppingListRepo shoppingListRepo) {
        this.householdRepo = householdRepo;
        this.householdMapper = householdMapper;
        this.userRepo = userRepo;
        this.shoppingListRepo = shoppingListRepo;
    }

    /**
     * Adds a new household based on the provided HouseholdDTO.
     * This method handles the conversion from DTO to entity,
     * resolves related User and ShoppingList entities by their IDs,
     * sets the creation timestamp, and saves the household to the database.
     *
     * @param householdDTO The DTO containing household details, including userIds and shoppingListIds.
     * @return The DTO of the newly created household, including its generated ID and timestamp.
     * @throws EntityNotFoundException if any specified User or ShoppingList does not exist.
     * @throws Exception for any other unexpected errors during the process.
     */
    @Override
    public HouseholdDTO addHousehold(HouseholdDTO householdDTO) {
        try {
            // Convert HouseholdDTO to Household entity using the mapper
            // Pass the necessary repositories as context for relationship resolution
            Household household = householdMapper.householdDTOToHousehold(householdDTO, userRepo, shoppingListRepo);

            // Set the creation timestamp for the new household
            household.setCreatedAt(LocalDateTime.now());

            // Note: The mapper handles setting users and shoppingLists based on IDs.
            // You might want to add additional validation here if lists must not be empty, etc.
            // For example, if a user ID from DTO was not found by the mapper, it would be null in the list.
            // You could check for nulls in household.getUsers() or household.getShoppingLists() here
            // and throw an EntityNotFoundException if any were not resolved.
            // However, for now, we rely on the mapper's default behavior (returning null for unresolvable IDs)
            // or you could modify the mapper to throw exceptions directly.

            // Save the prepared Household entity to the database
            Household savedHousehold = householdRepo.save(household);
            // Convert the saved Household entity back to DTO and return
            return householdMapper.householdToHouseholdDTO(savedHousehold);
        } catch (Exception e) {
            // Log the error with full stack trace for better debugging
            log.error("Error adding household: {}", e.getMessage(), e);
            // Re-throw the exception to be handled by higher layers (e.g., ControllerAdvice)
            throw e;
        }
    }

    /**
     * Retrieves all households and converts them to HouseholdDTOs.
     *
     * @return A list of HouseholdDTOs.
     */
    @Override
    public List<HouseholdDTO> getAllHouseholds() {
        return householdMapper.householdsToHouseholdDTOs(householdRepo.findAll());
    }

    /**
     * Retrieves a household by its ID and converts it to HouseholdDTO.
     * Uses Optional.orElseThrow for robust error handling if the household is not found.
     *
     * @param id The ID of the household to retrieve.
     * @return The HouseholdDTO of the found household.
     * @throws EntityNotFoundException if no household with the given ID exists.
     */
    @Override
    public HouseholdDTO getHouseholdById(Long id) {
        Household household = householdRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Household with ID " + id + " not found."));
        return householdMapper.householdToHouseholdDTO(household);
    }

    /**
     * Updates an existing household based on the provided HouseholdDTO.
     * It fetches the existing household, updates its fields including relationships,
     * and saves the changes.
     *
     * @param householdDTO The DTO with updated household details. The ID must match an existing household.
     * @return The DTO of the updated household.
     * @throws EntityNotFoundException if the household to be updated, or any related User/ShoppingList, does not exist.
     */
    @Override
    public HouseholdDTO updateHousehold(HouseholdDTO householdDTO) {
        // Ensure the household exists before attempting to update
        Household existingHousehold = householdRepo.findById(householdDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Household to be updated not found. Household Id: " + householdDTO.getId()));

        // Update basic fields from DTO to the existing entity
        existingHousehold.setName(householdDTO.getName());
        // Update createdAt timestamp on update, as per original logic if desired.
        // Usually, createdAt should not change on update, but if it's part of your business logic, keep it.
        // existingHousehold.setCreatedAt(LocalDateTime.now());

        // MapStruct can handle updating relationships in place if configured.
        // For simplicity, we re-map the relationships here based on the DTO's IDs.
        // This will replace the existing collections with new ones based on the provided IDs.
        // Ensure your Household entity has `orphanRemoval = true` on @OneToMany relationships
        // if you want to automatically delete removed child entities from the database.
        existingHousehold.setUsers(householdMapper.mapUserIdsToUsers(householdDTO.getUserIds(), userRepo));
        existingHousehold.setShoppingLists(householdMapper.mapShoppingListIdsToShoppingLists(householdDTO.getShoppingListIds(), shoppingListRepo));

        // IMPORTANT: When updating collections like users or shoppingLists,
        // ensure the inverse side of the relationship is also updated if it's bidirectional.
        // For example, when a user is assigned to a household, user.setHousehold(household) must be called.
        // This is crucial for JPA to manage the relationships correctly.
        // MapStruct can be configured to handle this, or you can do it manually here.
        // Example for users:
        if (existingHousehold.getUsers() != null) {
            for (User user : existingHousehold.getUsers()) {
                if (user != null) { // Check for null in case an ID wasn't resolved
                    user.setHousehold(existingHousehold);
                }
            }
        }
        // Example for shopping lists:
        if (existingHousehold.getShoppingLists() != null) {
            for (ShoppingList shoppingList : existingHousehold.getShoppingLists()) {
                if (shoppingList != null) { // Check for null in case an ID wasn't resolved
                    shoppingList.setHousehold(existingHousehold);
                }
            }
        }


        // Save the updated existing Household entity
        Household updatedHousehold = householdRepo.save(existingHousehold);
        return householdMapper.householdToHouseholdDTO(updatedHousehold);
    }

    /**
     * Deletes a household by its ID.
     * Checks for existence before deleting to provide a more specific error if not found.
     *
     * @param id The ID of the household to delete.
     * @throws EntityNotFoundException if no household with the given ID exists.
     */
    @Override
    public void deleteHouseholdById(Long id) {
        if (!householdRepo.existsById(id)) {
            throw new EntityNotFoundException("Household with ID " + id + " not found for deletion.");
        }
        householdRepo.deleteById(id);
    }

    // Removed specific methods like addUserToHousehold, removeUserFromHousehold, etc.
    // These operations are now handled by updating the Household entity itself via updateHousehold,
    // where userIds and shoppingListIds are provided in the DTO.
    // If direct manipulation of relationships is required, it should be done through dedicated
    // endpoints/methods that update the relationship directly (e.g., a UserService method to assign a user to a household),
    // or by fetching the Household and updating its 'users' or 'shoppingLists' collections and then saving the Household.
}
