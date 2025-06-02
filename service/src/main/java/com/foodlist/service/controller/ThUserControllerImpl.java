package com.foodlist.service.controller;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO;
import com.foodlist.service.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ThUserControllerImpl {

    ItemService itemService;

    public ThUserControllerImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public ModelAndView index() {
        List<ItemDisplayDTO> items = itemService.getAllItems();
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("items", items);
        return modelAndView;
    }

}
