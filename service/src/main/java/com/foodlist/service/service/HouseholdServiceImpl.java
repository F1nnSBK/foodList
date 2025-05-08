package com.foodlist.service.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.HouseholdRepo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class HouseholdServiceImpl implements HouseholdService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HouseholdServiceImpl.class);
    private final HouseholdRepo householdRepo;

    public HouseholdServiceImpl(HouseholdRepo householdRepo) {
        this.householdRepo = householdRepo;
    }

    public void createHousehold(Household household) {
        log.info("Creating household: {}", household.getName());
        try{
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to create household: {}", household.getName(), e);
        }
    }

    public Household getHouseholdById(Long id) {
        log.info("Retrieving household by id: {}", id);

        // Versuche Household zu finden, wirf ResourceNotFoundException, wenn nicht gefunden
        return householdRepo
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Household.class, "Household not found with ID: " + id));
    }

    public List<Household> getAllHouseholds() {
        return householdRepo.findAll();
    }

    public Household updateHousehold(Household household) {
        log.info("Updating household: {}", household.getName());

        if (household == null) {
            log.error("Cannot update a null household entity.");
            throw new IllegalArgumentException("Household entity to update cannot be null.");
        }

        if (household.getId() == null) {
            log.error("Household entity must have an ID to be updated.");
            throw new IllegalArgumentException("Household entity must have an ID to be updated.");
        } else {
            if (!householdRepo.existsById(household.getId())) {
                log.error("Household with ID {} not found for update.", household.getId());
                throw new IllegalArgumentException("Household with ID " + household.getId() + " not found for update.");
            }
        }
        Household updatedHousehold = householdRepo.save(household);
        log.info("Successfully updated household with ID: {}", updatedHousehold.getId());

        return updatedHousehold;
    }

    public void deleteHouseholdById(Long householdId) {
        log.info("Deleting household: {}", householdId);
        try {
            householdRepo.deleteById(householdId);
        } catch (EmptyResultDataAccessException e) {
            log.info("No such household: {}", householdId);
        }
    }

    public void addUserToHousehold(User user, Household household) {
        log.info("Adding user to household: {}", household.getName());
        try {
            household.getUsers().add(user);
            user.setHousehold(household);
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to add user to household: {}", household.getName(), e);
            e.printStackTrace();
        }
    }

    public void removeUserFromHousehold(User user, Household household) {
        log.info("Removing user from household: {}", household.getName());
        try {
            household.getUsers().remove(user);
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to remove user from household: {}", household.getName(), e);
        }
    }

    public void addShoppingListToHousehold(ShoppingList shoppingList, Household household) {
        log.info("Adding shoppingList to household: {}", household.getName());
        try {
            household.getShoppingLists().add(shoppingList);
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to add shoppingList to household: {}", household.getName(), e);
        }
    }

    public void removeShoppingListFromHousehold(ShoppingList shoppingList, Household household) {
        log.info("Removing shoppingList from household: {}", household.getName());
        try {
            household.getShoppingLists().remove(shoppingList);
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to remove shoppingList from household: {}", household.getName(), e);
        }
    }

}
