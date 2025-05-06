package com.foodlist.service.repository;

import com.foodlist.service.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingListRepo extends JpaRepository<ShoppingList, Long> {
}
