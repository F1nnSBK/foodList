package com.foodlist.service.repository;

import com.foodlist.service.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRepo extends JpaRepository<Household, Long> {
}
