package com.foodlist.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.dto.ItemDisplayDTO; // Importieren Sie ItemDisplayDTO
import com.foodlist.service.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ItemDTOs für POST/PUT-Tests
    ItemDTO item01;
    ItemDTO item02;

    // ItemDisplayDTOs für GET-Tests (da der Service diese jetzt zurückgibt)
    ItemDisplayDTO itemDisplay01;
    ItemDisplayDTO itemDisplay02;
    List<ItemDisplayDTO> itemDisplayList;


    private final String controllerPath = "/api/v1/items";

    @BeforeEach
    void setUp() {
        // Initialisierung von ItemDTOs (für addItem, updateItem)
        item01 = new ItemDTO(
                1L,                 // Item ID
                "Apfel",            // Name
                5,                  // Quantity
                false,              // Is checked
                LocalDateTime.now(),// CreatedAt
                101L,               // Added By User ID
                201L                // Shopping List ID
        );
        item02 = new ItemDTO(
                2L,                 // Item ID
                "Banane",           // Name
                1,                  // Quantity
                true,               // Is checked
                LocalDateTime.now(),// CreatedAt
                102L,               // Added By User ID
                201L                // Shopping List ID
        );

        // Initialisierung von ItemDisplayDTOs (für getAllItems, getItemById)
        itemDisplay01 = new ItemDisplayDTO(
                1L,
                "Apfel",
                5,
                false,
                LocalDateTime.now(),
                101L,
                "Benutzer A", // Beispiel-Benutzername
                201L,
                "Einkaufsliste X" // Beispiel-Einkaufslistenname
        );
        itemDisplay02 = new ItemDisplayDTO(
                2L,
                "Banane",
                1,
                true,
                LocalDateTime.now(),
                102L,
                "Benutzer B", // Beispiel-Benutzername
                201L,
                "Einkaufsliste X" // Beispiel-Einkaufslistenname
        );
        itemDisplayList = List.of(itemDisplay01, itemDisplay02);
    }

    @Test
    public void shouldSuccessfullyCreateItem() throws Exception {
        // Mock the service call to return item01 when addItem is called with any ItemDTO
        when(itemService.addItem(any(ItemDTO.class))).thenReturn(item01);

        // Perform the POST request
        this.mockMvc.perform(post(controllerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item01))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(item01.getId().intValue())))
                .andExpect(jsonPath("$.name", is(item01.getName())))
                .andExpect(jsonPath("$.quantity", is(item01.getQuantity())))
                .andExpect(jsonPath("$.checked", is(item01.isChecked())))
                .andExpect(jsonPath("$.addedByUserId", is(item01.getAddedByUserId().intValue())))
                .andExpect(jsonPath("$.shoppingListId", is(item01.getShoppingListId().intValue())))
                .andDo(print());

        verify(itemService).addItem(any(ItemDTO.class));
    }

    @Test
    public void shouldGetAllItems() throws Exception {
        // Mock the service call to return the list of ItemDisplayDTOs
        when(itemService.getAllItems()).thenReturn(itemDisplayList);

        // Perform the GET request to retrieve all items
        this.mockMvc.perform(get(controllerPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(itemDisplayList.size())))
                .andExpect(jsonPath("$[0].name", is(itemDisplay01.getName())))
                .andExpect(jsonPath("$[0].addedByUserName", is(itemDisplay01.getAddedByUserName()))) // NEU: Check username
                .andExpect(jsonPath("$[0].shoppingListName", is(itemDisplay01.getShoppingListName()))) // NEU: Check shopping list name
                .andExpect(jsonPath("$[1].name", is(itemDisplay02.getName())))
                .andDo(print());

        verify(itemService).getAllItems();
    }

    @Test
    public void shouldGetItemById() throws Exception {
        Long itemId = itemDisplay01.getId();
        // Mock the service call to return ItemDisplayDTO
        when(itemService.getItemById(itemId)).thenReturn(itemDisplay01);

        // Perform the GET request for a specific item
        this.mockMvc.perform(get(controllerPath + "/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is(itemDisplay01.getName())))
                .andExpect(jsonPath("$.addedByUserName", is(itemDisplay01.getAddedByUserName()))) // NEU: Check username
                .andExpect(jsonPath("$.shoppingListName", is(itemDisplay01.getShoppingListName()))) // NEU: Check shopping list name
                .andDo(print());

        verify(itemService).getItemById(itemId);
    }

    @Test
    public void shouldReturnNotFoundWhenItemByIdDoesNotExist() throws Exception {
        Long nonExistentId = 99L;
        // Mock the service call to throw EntityNotFoundException
        when(itemService.getItemById(nonExistentId)).thenThrow(new jakarta.persistence.EntityNotFoundException("Item not found"));

        // Perform the GET request for a non-existent item
        this.mockMvc.perform(get(controllerPath + "/{itemId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(itemService).getItemById(nonExistentId);
    }

    @Test
    public void shouldSuccessfullyUpdateItem() throws Exception {
        Long itemId = item01.getId();
        ItemDTO updatedItemDTO = new ItemDTO(
                itemId,
                "Aktualisierter Apfel",
                6,
                true,
                item01.getAddedAt(),
                101L,
                201L
        );
        // Mock the service call to return the updated DTO
        when(itemService.updateItem(any(ItemDTO.class))).thenReturn(updatedItemDTO);

        // Perform the PUT request to update the item
        this.mockMvc.perform(put(controllerPath + "/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is(updatedItemDTO.getName())))
                .andExpect(jsonPath("$.quantity", is(updatedItemDTO.getQuantity())))
                .andExpect(jsonPath("$.checked", is(updatedItemDTO.isChecked())))
                .andExpect(jsonPath("$.addedByUserId", is(updatedItemDTO.getAddedByUserId().intValue())))
                .andExpect(jsonPath("$.shoppingListId", is(updatedItemDTO.getShoppingListId().intValue())))
                .andDo(print());

        verify(itemService).updateItem(any(ItemDTO.class));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingNonExistentItem() throws Exception {
        Long nonExistentId = 99L;
        ItemDTO itemToUpdate = new ItemDTO(
                nonExistentId,
                "Non Existent Item",
                1,
                false,
                LocalDateTime.now(),
                101L,
                201L
        );
        // Mock the service call to throw EntityNotFoundException
        when(itemService.updateItem(any(ItemDTO.class))).thenThrow(new jakarta.persistence.EntityNotFoundException("Item not found for update"));

        // Perform the PUT request
        this.mockMvc.perform(put(controllerPath + "/{itemId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(itemService).updateItem(any(ItemDTO.class));
    }

    @Test
    public void shouldSuccessfullyDeleteItem() throws Exception {
        Long itemId = item01.getId();
        // Mock the service call to do nothing when delete is called (void method)
        // No need for when().thenReturn() for void methods

        // Perform the DELETE request
        this.mockMvc.perform(delete(controllerPath + "/{itemId}", itemId))
                .andExpect(status().isNoContent())
                .andDo(print());

        // Verify that the deleteItemById method of the service was called
        verify(itemService).deleteItemById(itemId);
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingNonExistentItem() throws Exception {
        Long nonExistentId = 99L;
        // Mock the service call to throw EntityNotFoundException
        doThrow(new jakarta.persistence.EntityNotFoundException("Item not found for deletion")).when(itemService).deleteItemById(nonExistentId);

        // Perform the DELETE request
        this.mockMvc.perform(delete(controllerPath + "/{itemId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andDo(print());

        // Verify the service method was called
        verify(itemService).deleteItemById(nonExistentId);
    }
}
