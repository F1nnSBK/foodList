package com.foodlist.service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.foodlist.service.model.Household}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDTO{
    private Long id;
    private String name;

    private LocalDateTime createdAt;

    private List<Long> userIds;

    private List<Long> shoppingListIds;
}