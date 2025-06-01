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
    Long id;
    private String username;
    String passwordHash;
    String name;
    boolean enabled;
    LocalDateTime createdAt;
}