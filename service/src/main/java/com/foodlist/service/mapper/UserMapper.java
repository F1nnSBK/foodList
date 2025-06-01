package com.foodlist.service.mapper;

import com.foodlist.service.dto.UserDTO;
import com.foodlist.service.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);
    List<UserDTO> usersToUserDTOs(List<User> users);

    @InheritInverseConfiguration
    User userDTOToUser(UserDTO userDTO);
    List<User> userDTOsToUsers(List<UserDTO> userDTOs);

}
