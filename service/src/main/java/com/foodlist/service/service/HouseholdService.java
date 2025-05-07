package com.foodlist.service.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;

public interface HouseholdService {
    void createHousehold(Household household);

    void deleteHousehold(Household household);

    void addUserToHousehold(User user, Household household);

    void addShoppingListToHousehold(ShoppingList shoppingList, Household household);
}
