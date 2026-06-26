package com.ivy.forum.service;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.dto.req.LoginRequest;
import com.ivy.forum.dto.req.RegisterRequest;
import com.ivy.forum.dto.resp.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthService 测试用例
 *
 * 用例ID: AUTH-SVC-001 ~ 004
 */
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void AUTH_SVC_001_register_should_succeed() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("123456");
        req.setNickname("测试用户");
        LoginResponse resp = authService.register(req);
        assertNotNull(resp);
        assertNotNull(resp.getToken());
        assertEquals("STUDENT", resp.getRole());
    }

    @Test
    void AUTH_SVC_002_register_existing_username_should_fail() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("admin");
        req.setPassword("123456");
        req.setNickname("重复用户");
        assertThrows(BusinessException.class, () -> authService.register(req));
    }

    @Test
    void AUTH_SVC_003_login_invalid_credentials_should_fail() {
        LoginRequest req = new LoginRequest();
        req.setUsername("nonexistent");
        req.setPassword("wrong");
        assertThrows(Exception.class, () -> authService.login(req));
    }

    @Test
    void AUTH_SVC_004_currentUser_unauthenticated_should_fail() {
        assertThrows(BusinessException.class, () -> authService.currentUser());
    }
}