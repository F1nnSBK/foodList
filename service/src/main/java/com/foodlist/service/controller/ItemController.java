package com.foodlist.service.controller;


import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/")
    public List<ItemDTO> getItemsAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemById(@PathVariable Long itemId) {
        try{
            return itemService.getItemById(itemId);
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO addItem(@Valid @RequestBody ItemDTO item) {
        try{
            return this.itemService.addItem(item);
        } catch (ConstraintViolationException cve) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{itemId}")
    public ItemDTO updateItem(@PathVariable("itemId") Long id,
                              @Valid @RequestBody ItemDTO item) {
        item.setId(id);
        try {
            return this.itemService.updateItem(item);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (NoSuchElementException | ConstraintViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemById(@PathVariable("itemId") Long id) {
        try {
            this.itemService.deleteItemById(id);
        } catch (NoSuchElementException nse) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
