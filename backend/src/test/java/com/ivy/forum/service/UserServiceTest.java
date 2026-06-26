package com.ivy.forum.service;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService 测试用例
 *
 * 用例ID: USER-SVC-001 ~ 004
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void USER_SVC_001_getUserById_should_return() {
        User u = userService.getUserById(1L);
        assertNotNull(u);
        assertNotNull(u.getUsername());
    }

    @Test
    void USER_SVC_002_getUserById_not_found_should_throw() {
        assertThrows(BusinessException.class, () -> userService.getUserById(99999L));
    }

    @Test
    void USER_SVC_003_updateProfile_should_change() {
        userService.updateProfile(1L, "新昵称", "http://example.com/avatar.png", "新签名");
        User u = userService.getUserById(1L);
        assertEquals("新昵称", u.getNickname());
        assertEquals("新签名", u.getSignature());
    }

    @Test
    void USER_SVC_004_ban_unban_should_toggle_status() {
        userService.banUser(2L);
        User u = userService.getUserById(2L);
        assertEquals("BANNED", u.getStatus());
        userService.unbanUser(2L);
        u = userService.getUserById(2L);
        assertEquals("NORMAL", u.getStatus());
    }
}