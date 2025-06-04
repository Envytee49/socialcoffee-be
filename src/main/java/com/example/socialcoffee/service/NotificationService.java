package com.example.socialcoffee.service;

import com.example.socialcoffee.domain.postgres.Notification;
import com.example.socialcoffee.domain.postgres.User;
import com.example.socialcoffee.dto.common.PageDtoIn;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.dto.response.NotificationDTO;
import com.example.socialcoffee.dto.response.ResponseMetaData;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.enums.NotificationStatus;
import com.example.socialcoffee.enums.NotificationType;
import com.example.socialcoffee.exception.NotFoundException;
import com.example.socialcoffee.repository.postgres.NotificationRepository;
import com.example.socialcoffee.repository.postgres.UserRepository;
import com.example.socialcoffee.utils.DateTimeUtil;
import com.example.socialcoffee.utils.ObjectUtil;
import com.example.socialcoffee.utils.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.example.socialcoffee.utils.ObjectUtil.objectToString;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final CacheableService cacheableService;

    private final NotificationRepository notificationRepository;

    public ResponseEntity<ResponseMetaData> getUserNotifications(User user, PageDtoIn pageDtoIn) {
        final List<Notification> notifications = user.getNotifications();
        if (CollectionUtils.isEmpty(notifications))
            return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                    Collections.emptyList()));
        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        List<Notification> pageResult = ObjectUtil.getPageResult(notifications,
                pageDtoIn.getPage() - 1,
                pageDtoIn.getSize());
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (final Notification notification : pageResult) {
            Object meta = ObjectUtil.stringToObject(objectMapper,
                    notification.getMeta(),
                    Object.class);
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .createdAt(DateTimeUtil.covertLocalDateToString(notification.getCreatedAt()))
                    .type(notification.getType())
                    .status(notification.getStatus())
                    .meta(meta)
                    .build();
            notificationDTOS.add(notificationDTO);
        }
        return ResponseEntity.ok().body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS),
                notificationDTOS));
    }

    public ResponseEntity<Long> getUnreadNotifications(User user) {
        Long count = NumberUtils.LONG_ZERO;
        final List<Notification> notifications = user.getNotifications();
        if (CollectionUtils.isEmpty(notifications)) return ResponseEntity.ok().body(count);
        for (final Notification notification : notifications) {
            if (notification.getStatus().equalsIgnoreCase(NotificationStatus.UNREAD.getValue())) count++;
        }
        return ResponseEntity.ok().body(count);
    }

    public void markAllNotificationsAsRead(User user) {
        user.getNotifications().forEach(
                n -> n.setStatus(NotificationStatus.READ.getValue())
        );
        userRepository.save(user);
    }

    public void markNotificationAsRead() {
        Notification notification = notificationRepository.findById(SecurityUtil.getUserId()).orElseThrow(NotFoundException::new);
        notification.setStatus(NotificationStatus.READ.getValue());
        notificationRepository.save(notification);
    }

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
            log.error("Error: {}", e.getMessage()); // lazy load
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
