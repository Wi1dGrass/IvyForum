package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.entity.Notification;
import com.ivy.forum.mapper.NotificationMapper;
import com.ivy.forum.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public Page<Notification> listByUser(Long userId, int page, int size) {
        return notificationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public long unreadCount(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .eq(Notification::getIsRead, 0));
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long notifyId) {
        LambdaUpdateWrapper<Notification> qw = new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getReceiverId, userId);
        if (notifyId != null) qw.eq(Notification::getNotifyId, notifyId);
        qw.setSql("is_read = 1");
        notificationMapper.update(null, qw);
    }

    @Override
    public void send(Long receiverId, Long senderId, String type, String targetType, Long targetId, String content) {
        Notification n = new Notification();
        n.setReceiverId(receiverId);
        n.setSenderId(senderId);
        n.setType(type);
        n.setTargetType(targetType);
        n.setTargetId(targetId);
        n.setContent(content);
        n.setIsRead(0);
        notificationMapper.insert(n);
    }
}