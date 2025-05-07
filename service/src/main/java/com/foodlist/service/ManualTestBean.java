package com.foodlist.service;

import com.foodlist.service.model.Household;
import com.foodlist.service.model.User;
import com.foodlist.service.service.HouseholdService;
import com.foodlist.service.service.ItemService;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ManualTestBean {

    ItemService itemService;
    HouseholdService householdService;

    public ManualTestBean(ItemService itemService, HouseholdService householdService) {
        this.itemService = itemService;
        this.householdService = householdService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createSampleHousehold() {
        User finn = new User("Finn", "test", true);
        Household household1 = new Household("Hertsch");
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
    }

}
