package com.ivy.forum.service;

import com.ivy.forum.entity.Notification;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface NotificationService {
    Page<Notification> listByUser(Long userId, int page, int size);
    long unreadCount(Long userId);
    void markRead(Long userId, Long notifyId);
    void send(Long receiverId, Long senderId, String type, String targetType, Long targetId, String content);
}