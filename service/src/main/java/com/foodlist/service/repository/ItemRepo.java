package com.foodlist.service.repository;

import com.foodlist.service.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long> {

    public Item findByName(String name);

}
