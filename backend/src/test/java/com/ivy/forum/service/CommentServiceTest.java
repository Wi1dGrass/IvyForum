package com.ivy.forum.service;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.entity.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommentService 测试用例
 *
 * 用例ID: CMT-SVC-001 ~ 005
 */
@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void CMT_SVC_001_create_comment_should_return_id() {
        Long id = commentService.createComment(1L, null, "测试评论", 1L);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void CMT_SVC_002_create_reply_should_have_rootId() {
        Long rootId = commentService.createComment(1L, null, "根评论", 1L);
        Long replyId = commentService.createComment(1L, rootId, "回复", 1L);
        assertTrue(replyId > 0);
    }

    @Test
    void CMT_SVC_003_listByPost_should_return_tree() {
        List<Comment> roots = commentService.listByPost(1L);
        assertNotNull(roots);
        // 验证返回结构
        for (Comment r : roots) {
            assertNull(r.getParentId(), "根评论 parentId 应为 null");
        }
    }

    @Test
    void CMT_SVC_004_delete_comment_should_flag_deleted() {
        Long id = commentService.createComment(1L, null, "即将删除", 1L);
        commentService.deleteComment(id, 1L);
        // 删除后所有评论列表应不包含它（isDeleted=1会被查询过滤）
        List<Comment> roots = commentService.listByPost(1L);
        boolean found = roots.stream().anyMatch(c -> c.getCommentId().equals(id));
        assertFalse(found, "已删除评论不应在树中");
    }

    @Test
    void CMT_SVC_005_delete_other_user_comment_should_fail() {
        Long id = commentService.createComment(1L, null, "他人评论", 1L);
        assertThrows(BusinessException.class, () -> commentService.deleteComment(id, 999L));
    }
}