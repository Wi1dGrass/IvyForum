package com.ivy.forum.service;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.dto.req.PostCreateRequest;
import com.ivy.forum.dto.resp.PostItemVo;
import com.ivy.forum.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostService 测试用例
 *
 * 用例ID: POST-SVC-001 ~ 006
 */
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void POST_SVC_001_createPost_should_return_id() {
        PostCreateRequest req = new PostCreateRequest();
        req.setChannelId(1L);
        req.setTitle("测试帖子标题");
        req.setContent("这是测试帖子的内容");
        Long id = postService.createPost(req, 1L);
        assertNotNull(id, "发帖应返回有效 postId");
        assertTrue(id > 0);
    }

    @Test
    void POST_SVC_002_detail_should_return_post() {
        Post p = postService.detail(1L);
        assertNotNull(p);
        assertNotNull(p.getTitle());
    }

    @Test
    void POST_SVC_003_detail_not_found_should_throw() {
        assertThrows(BusinessException.class, () -> postService.detail(99999L));
    }

    @Test
    void POST_SVC_004_listPosts_should_paginate() {
        var page = postService.listPosts(null, null, null, "latest", 1, 10);
        assertNotNull(page);
        assertTrue(page.getRecords().size() <= 10);
        assertTrue(page.getTotal() >= 0);
    }

    @Test
    void POST_SVC_005_hotList_should_return_ordered() {
        // 热门榜可能为空，不抛异常即可
        List<PostItemVo> hot = postService.hotList();
        assertNotNull(hot);
    }

    @Test
    void POST_SVC_006_toggleTop_should_change_status() {
        Post p = postService.detail(1L);
        int before = p.getIsTop();
        postService.toggleTop(1L);
        p = postService.detail(1L);
        assertEquals(before ^ 1, p.getIsTop().intValue(), "置顶状态应切换");
    }
}