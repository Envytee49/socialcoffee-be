package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.repository.UserRepository;
import com.example.socialcoffee.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BaseService {
    @Autowired
    protected UserRepository userRepository;
    public User getCurrentUser() {
        Long userId = SecurityUtil.getUserId();
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        return optionalUser.orElse(null);
    }
}
