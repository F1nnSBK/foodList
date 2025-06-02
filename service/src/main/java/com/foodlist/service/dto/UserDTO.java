package com.foodlist.service.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.foodlist.service.model.User}
 */
@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Long id;

    private String username;

    private String name;

    private boolean enabled;

    private LocalDateTime createdAt;

    private Long householdId;
}