package com.foodlist.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.Item;
import com.foodlist.service.model.ShoppingList;
import com.foodlist.service.model.User;
import com.foodlist.service.repository.ShoppingListRepo;
import com.foodlist.service.service.HouseholdService;
import com.foodlist.service.service.ItemService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ManualTestBean {

    ItemService itemService;
    HouseholdService householdService;
    private final ShoppingListRepo shoppingListRepo;

    public ManualTestBean(ItemService itemService, HouseholdService householdService,
                          ShoppingListRepo shoppingListRepo) {
        this.itemService = itemService;
        this.householdService = householdService;
        this.shoppingListRepo = shoppingListRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createSampleHousehold() {
        // Household and users
        User finn = new User("Finn", "test", true);
        Household household1 = new Household("Hertsch");
        finn.setHousehold(household1);
        householdService.addUserToHousehold(finn, household1);

        List<User> users = new ArrayList<>();
        users.add(new User("Lill", "test", true));
        users.add(new User("Tom", "test", true));
        users.add(new User("Sofi", "test", true));
        Household household2 = new Household("Schalsky", users);
        for(User user : users) {
            user.setHousehold(household2);
        }
        householdService.createHousehold(household2);

        // Items
        Item tomato = new Item("Tomato", 1, true, LocalDate.now());
        // Shopping List
        ShoppingList shoppingListH1 = new ShoppingList(
                household1,
                "Einkaufslischde",
                true,
                LocalDate.now()
        );
        shoppingListH1.setItems(List.of(tomato));
        tomato.setShoppingList(shoppingListH1);

        shoppingListH1.setHousehold(household1);
        shoppingListRepo.save(shoppingListH1);
        ShoppingList shoppingListH2 = new ShoppingList(
                household2,
                "Lischte",
                true,
                LocalDate.now()
        );
        shoppingListH2.setHousehold(household2);
        shoppingListRepo.save(shoppingListH2);


    }

}
