package com.foodlist.service.service;

import com.foodlist.service.dto.ItemDTO;
import com.foodlist.service.mapper.ItemMapper;
import com.foodlist.service.model.Item;
import com.foodlist.service.repository.ItemRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    ItemMapper itemMapper;
    ItemRepo itemRepo;

    public ItemServiceImpl(ItemMapper itemMapper, ItemRepo itemRepo) {
        this.itemMapper = itemMapper;
        this.itemRepo = itemRepo;
    }

    @Override
    public ItemDTO addItem(ItemDTO itemDTO) {
        try {
            Item tmp = this.itemMapper.itemDTOToItem(itemDTO);
            tmp.setAddedAt(LocalDateTime.now());
            return this.itemMapper.itemToItemDTO(itemRepo.save(tmp));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<ItemDTO> getAllItems() {
        return this.itemMapper.itemsToItemDTOs(this.itemRepo.findAll());
    }

    @Override
    public ItemDTO getItemById(Long id) {
        return this.itemMapper.itemToItemDTO(this.itemRepo.findById(id).get());
    }

    @Override
    public ItemDTO updateItem(ItemDTO itemDTO) {
        if(itemRepo.existsById(itemDTO.getId())) {
            Item tmp = this.itemMapper.itemDTOToItem(itemDTO);
            tmp.setAddedAt(LocalDateTime.now());
            return this.itemMapper.itemToItemDTO(this.itemRepo.save(tmp));
        } else {
            throw new EntityNotFoundException("Item to be updated not found. Item Id: " + itemDTO.getId());
        }
    }

    @Override
    public void deleteItemById(Long id) {
        this.itemRepo.deleteById(id);
    }

}
