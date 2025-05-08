package com.foodlist.service.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;

import java.util.List;

public interface HouseholdService {
    void createHousehold(Household household);

    Household getHouseholdById(Long id);

    List<Household> getAllHouseholds();

    Household updateHousehold(Household household);

    void deleteHouseholdById(Long householdId);

    void addUserToHousehold(User user, Household household);

    void removeUserFromHousehold(User user, Household household);

    void addShoppingListToHousehold(ShoppingList shoppingList, Household household);

    void removeShoppingListFromHousehold(ShoppingList shoppingList, Household household);
}
