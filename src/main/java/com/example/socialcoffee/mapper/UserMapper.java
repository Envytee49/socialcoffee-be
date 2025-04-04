package com.example.socialcoffee.mapper;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.dto.response.UserDTO;

public interface UserMapper {
    UserDTO toUserDTO(User user);
}
