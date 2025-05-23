package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.User;
import com.example.socialcoffee.enums.NotificationStatus;
import com.example.socialcoffee.enums.NotificationType;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.socialcoffee.utils.ObjectUtil.objectToString;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final CacheableService cacheableService;

    public void pushNotiToUsersWhenFinishCreatingShop(String id,
                                                      String name,
                                                      String path) {
        List<User> activeUsers = cacheableService.getActiveUsers();
        Map<String, String> meta = new HashMap<>();
        meta.put("id",
                id);
        meta.put("name",
                name);
        meta.put("path",
                path);
        for (final User activeUser : activeUsers) {
            activeUser.addNotification("New coffee shop",
                    NotificationType.COFFEE_SHOP.getValue(),
                    NotificationStatus.UNREAD.getValue(),
                    "A new cofee shop is inserted! Check it out",
                    objectToString(objectMapper, meta));
        }
        userRepository.saveAll(activeUsers);
    }


    public void pushNotiToUsersWhenFinishUpdatingShop(String id,
                                                      String name,
                                                      String path) {
        try {
            log.info("Start adding new notification for users");
            List<User> activeUsers = cacheableService.getActiveUsers();
            Map<String, String> meta = new HashMap<>();
            meta.put("id",
                    id);
            meta.put("name",
                    name);
            meta.put("path",
                    path);
            String message = String.format("%s was edited! Check the change",
                    name);
            for (final User activeUser : activeUsers) {
                activeUser.addNotification("Coffee shop edit",
                        NotificationType.COFFEE_SHOP.getValue(),
                        NotificationStatus.UNREAD.getValue(),
                        message,
                        objectToString(objectMapper, meta));
            }
            userRepository.saveAll(activeUsers);
            log.info("Finish adding new notification for users");
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

    }

    public void pushNotiToUsersWhenSuccessRegister(User saved) {
        Map<String, String> meta = new HashMap<>();
        meta.put("id",
                saved.getId().toString());
        meta.put("name",
                saved.getDisplayName());
        String message = String.format("Hi %s! Welcome to the system!",
                saved.getDisplayName());
        saved.addNotification("New comer",
                NotificationType.USER.getValue(),
                NotificationStatus.UNREAD.getValue(),
                message,
                objectToString(objectMapper, meta));
        userRepository.save(saved);
    }

    public void pushNotiToUsersWhenApproveContribution(User user,
                                                       final String name) {
        String message = String.format("Hi %s! Your contribution for coffee shop %s was approved! Thank you for your contribution",
                user.getDisplayName(),
                name);
        user.addNotification("Contribution approved",
                NotificationType.USER.getValue(),
                NotificationStatus.UNREAD.getValue(),
                message,
                null);
        userRepository.save(user);
    }

    public void pushNotiToUsersWhenRejectContribution(User user,
                                                      final String name) {
        String message = String.format("Hi %s! Your contribution for coffee shop %s was rejected! Please check admin comment",
                user.getDisplayName(),
                name);
        user.addNotification("Contribution rejected",
                NotificationType.USER.getValue(),
                NotificationStatus.UNREAD.getValue(),
                message,
                null);
        userRepository.save(user);
    }

    public void pushNotiToAdminWhenContribute(String username, String cfName) {
        String message = String.format("%s contributed a new coffee shop: %s",
                username,
                cfName);
        User admin = userRepository.findByUserId(0L).get();
        admin.addNotification("New contribution",
                NotificationType.USER.getValue(),
                NotificationStatus.UNREAD.getValue(),
                message,
                null);
        userRepository.save(admin);
    }

    public void pushNotiToAdminWhenSuggestAnEdit(String displayName,
                                                 String name) {
        String message = String.format("%s suggested an edit for coffee shop: %s",
                displayName,
                name);
        User admin = userRepository.findByUserId(0L).get();
        admin.addNotification("Edit suggestion",
                NotificationType.USER.getValue(),
                NotificationStatus.UNREAD.getValue(),
                message,
                null);
        userRepository.save(admin);
    }
}
