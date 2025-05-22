package com.example.socialcoffee.controller;

import com.example.socialcoffee.constants.CommonConstant;
import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.enums.Status;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.exception.UnauthorizedException;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class BaseController {
    @Autowired
    public HttpServletRequest request;

    @Autowired
    protected UserRepository userRepository;

    public Double getLatitude() {
        return NumberUtil.parseDouble(Objects.toString(request
                .getHeader(CommonConstant.LATITUDE), StringUtils.EMPTY), null);
    }

    public Double getLongitude() {
        return NumberUtil.parseDouble(Objects.toString(request
                .getHeader(CommonConstant.LONGITUDE), StringUtils.EMPTY), null);
    }

    public User getCurrentUser(String displayName) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            throw new UnauthorizedException();
        user = (StringUtils.isNotBlank(displayName) && !displayName.equalsIgnoreCase(user.getDisplayName()))
                ? userRepository.findByDisplayNameAndStatus(displayName,
                Status.ACTIVE.getValue())
                : user;
        if (Objects.isNull(user)) {
            throw new NotFoundException();
        }
        return user;
    }

    public User getCurrentUser() {
        Long userId = SecurityUtil.getUserId();
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty())
            throw new UnauthorizedException();
        return optionalUser.get();
    }

    public User getCurrentUser(Long userId) {
        User user = getCurrentUser();
        if (Objects.isNull(user))
            throw new UnauthorizedException();
        user = (Objects.nonNull(userId) && !Objects.equals(user.getId(),
                userId))
                ? userRepository.findByIdAndStatus(userId,
                Status.ACTIVE.getValue())
                : user;
        if (Objects.isNull(user))
            throw new NotFoundException();
        return user;
    }
}
