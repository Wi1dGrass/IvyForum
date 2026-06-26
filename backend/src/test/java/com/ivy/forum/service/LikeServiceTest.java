package com.ivy.forum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LikeService 测试用例
 *
 * 用例ID: LIKE-SVC-001 ~ 004
 */
@SpringBootTest
class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @Test
    void LIKE_SVC_001_toggle_add_should_like() {
        likeService.toggle(999L, "POST", 999L);
        boolean s = likeService.status(999L, "POST", 999L);
        assertTrue(s, "添加点赞后状态应为 true");
    }

    @Test
    void LIKE_SVC_002_toggle_remove_should_unlike() {
        likeService.toggle(999L, "POST", 998L);
        assertTrue(likeService.status(999L, "POST", 998L));
        likeService.toggle(999L, "POST", 998L);
        assertFalse(likeService.status(999L, "POST", 998L), "再次切换应取消点赞");
    }

    @Test
    void LIKE_SVC_003_status_no_like_should_false() {
        assertFalse(likeService.status(1L, "POST", 99999L));
    }

    @Test
    void LIKE_SVC_004_toggle_comment_should_work() {
        likeService.toggle(1L, "COMMENT", 1L);
        assertTrue(likeService.status(1L, "COMMENT", 1L));
    }
}