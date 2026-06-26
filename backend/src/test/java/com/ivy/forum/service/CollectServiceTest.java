package com.ivy.forum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CollectService 测试用例
 *
 * 用例ID: COLL-SVC-001 ~ 003
 */
@SpringBootTest
class CollectServiceTest {

    @Autowired
    private CollectService collectService;

    @Test
    void COLL_SVC_001_toggle_add_should_collect() {
        collectService.toggle(999L, 999L);
        assertTrue(collectService.status(999L, 999L));
    }

    @Test
    void COLL_SVC_002_toggle_remove_should_uncollect() {
        collectService.toggle(999L, 998L);
        assertTrue(collectService.status(999L, 998L));
        collectService.toggle(999L, 998L);
        assertFalse(collectService.status(999L, 998L));
    }

    @Test
    void COLL_SVC_003_userCollectPostIds_should_return_list() {
        collectService.toggle(888L, 1L);
        List<Long> ids = collectService.userCollectPostIds(888L);
        assertNotNull(ids);
        assertTrue(ids.contains(1L));
    }
}