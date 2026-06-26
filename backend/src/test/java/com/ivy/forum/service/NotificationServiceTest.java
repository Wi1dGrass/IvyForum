package com.ivy.forum.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationService 测试用例
 *
 * 用例ID: NOTIF-SVC-001 ~ 004
 */
@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void NOTIF_SVC_001_send_should_create() {
        notificationService.send(1L, 2L, "COMMENT", "POST", 1L, "有人评论了你的帖子");
        Page<Notification> p = notificationService.listByUser(1L, 1, 10);
        assertTrue(p.getTotal() > 0);
    }

    @Test
    void NOTIF_SVC_002_unreadCount_should_be_non_negative() {
        long count = notificationService.unreadCount(1L);
        assertTrue(count >= 0);
    }

    @Test
    void NOTIF_SVC_003_markRead_should_clear() {
        notificationService.send(3L, 2L, "COMMENT", "POST", 1L, "通知测试");
        long before = notificationService.unreadCount(3L);
        assertTrue(before >= 1);
        notificationService.markRead(3L, null);
        assertEquals(0, notificationService.unreadCount(3L));
    }

    @Test
    void NOTIF_SVC_004_listByUser_should_page() {
        Page<Notification> p = notificationService.listByUser(1L, 1, 5);
        assertNotNull(p);
        assertTrue(p.getRecords().size() <= 5);
    }
}