package com.foodlist.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.foodlist.service.model.Household}
 */
@Getter
@Setter
@AllArgsConstructor
public class HouseholdDTO{
    Long id;
    String name;
    LocalDateTime createdAt;
}