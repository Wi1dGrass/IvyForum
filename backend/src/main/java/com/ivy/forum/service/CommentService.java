package com.ivy.forum.service;

import com.ivy.forum.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> listByPost(Long postId);
    Long createComment(Long postId, Long parentId, String content, Long authorId);
    void deleteComment(Long commentId, Long userId);
}