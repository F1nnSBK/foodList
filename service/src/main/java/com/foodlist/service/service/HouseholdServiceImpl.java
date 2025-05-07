package com.foodlist.service.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.HouseholdRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;


@Service
public class HouseholdServiceImpl implements HouseholdService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HouseholdServiceImpl.class);
    private final HouseholdRepo householdRepo;

    public HouseholdServiceImpl(HouseholdRepo householdRepo) {
        this.householdRepo = householdRepo;
    }

    @Override
    public void createHousehold(Household household) {
        log.info("Creating household: {}", household.getName());
        try{
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to create household: {}", household.getName(), e);
        }
    }

//    public void updateHousehold(Household household) {
//        log.info("Updating household: {}", household.getName());
//        householdRepo.save(household);
//    }

    @Override
    public void deleteHousehold(Household household) {
        log.info("Deleting household: {}", household.getName());
        try {
            householdRepo.deleteById(household.getId());
        } catch (EmptyResultDataAccessException e) {
            log.info("No such household: {}", household.getName());
        }
    }

    @Override
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

    public void addShoppingListToHousehold(ShoppingList shoppingList, Household household) {
        log.info("Adding shoppingList to household: {}", household.getName());
        try {
            household.getShoppingLists().add(shoppingList);
            householdRepo.save(household);
        } catch (Exception e) {
            log.error("Failed to add shoppingList to household: {}", household.getName(), e);
        }
    }

}
