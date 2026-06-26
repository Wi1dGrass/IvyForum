package com.ivy.forum.service;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.entity.Comment;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostMapper postMapper;

    private Long testPostId;

    @BeforeEach
    void setup() {
        Post p = new Post();
        p.setAuthorId(1L);
        p.setChannelId(1L);
        p.setTitle("测试帖子");
        p.setContent("测试内容");
        p.setStatus("NORMAL");
        postMapper.insert(p);
        testPostId = p.getPostId();
    }

    @Test
    void CMT_SVC_001_create_comment_should_return_id() {
        Long id = commentService.createComment(testPostId, null, "测试评论", 1L);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void CMT_SVC_002_create_reply_should_have_rootId() {
        Long rootId = commentService.createComment(testPostId, null, "根评论", 1L);
        Long replyId = commentService.createComment(testPostId, rootId, "回复", 1L);
        assertTrue(replyId > 0);
    }

    @Test
    void CMT_SVC_003_listByPost_should_return_tree() {
        commentService.createComment(testPostId, null, "根评论", 1L);
        List<Comment> roots = commentService.listByPost(testPostId);
        assertNotNull(roots);
        for (Comment r : roots) {
            assertNull(r.getParentId(), "根评论 parentId 应为 null");
        }
    }

    @Test
    void CMT_SVC_004_delete_comment_should_flag_deleted() {
        Long id = commentService.createComment(testPostId, null, "即将删除", 1L);
        commentService.deleteComment(id, 1L);
        List<Comment> roots = commentService.listByPost(testPostId);
        boolean found = roots.stream().anyMatch(c -> c.getCommentId().equals(id));
        assertFalse(found, "已删除评论不应在树中");
    }

    @Test
    void CMT_SVC_005_delete_other_user_comment_should_fail() {
        Long id = commentService.createComment(testPostId, null, "他人评论", 1L);
        assertThrows(BusinessException.class, () -> commentService.deleteComment(id, 999L));
    }
}