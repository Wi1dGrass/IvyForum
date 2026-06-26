package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ivy.forum.common.BusinessException;
import com.ivy.forum.common.ErrorCode;
import com.ivy.forum.entity.Comment;
import com.ivy.forum.mapper.CommentMapper;
import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Override
    public List<Comment> listByPost(Long postId) {
        List<Comment> all = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getIsDeleted, 0)
                        .orderByAsc(Comment::getCreateTime));

        Map<Long, Comment> map = new LinkedHashMap<>();
        List<Comment> roots = new ArrayList<>();
        for (Comment c : all) {
            c.setChildren(new ArrayList<>());
            map.put(c.getCommentId(), c);
            if (c.getParentId() == null) {
                roots.add(c);
            }
        }
        for (Comment c : all) {
            if (c.getParentId() != null && map.containsKey(c.getParentId())) {
                map.get(c.getParentId()).getChildren().add(c);
            }
        }
        return roots;
    }

    @Override
    @Transactional
    public Long createComment(Long postId, Long parentId, String content, Long authorId) {
        if (postMapper.selectById(postId) == null) throw new BusinessException(ErrorCode.NOT_FOUND);

        Comment c = new Comment();
        c.setPostId(postId);
        c.setParentId(parentId);
        c.setAuthorId(authorId);
        c.setContent(content);
        if (parentId != null) {
            Comment parent = commentMapper.selectById(parentId);
            if (parent == null) throw new BusinessException(ErrorCode.NOT_FOUND);
            c.setRootId(parent.getRootId() != null ? parent.getRootId() : parent.getCommentId());
        }
        c.setLikeCount(0);
        commentMapper.insert(c);
        c.setFloor(c.getCommentId().intValue());

        postMapper.update(null, new LambdaUpdateWrapper<com.ivy.forum.entity.Post>()
                .eq(com.ivy.forum.entity.Post::getPostId, postId)
                .setSql("comment_count = comment_count + 1"));

        return c.getCommentId();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment c = commentMapper.selectById(commentId);
        if (c == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        if (!c.getAuthorId().equals(userId)) throw new BusinessException(ErrorCode.FORBIDDEN);
        c.setIsDeleted(1);
        commentMapper.updateById(c);
        postMapper.update(null, new LambdaUpdateWrapper<com.ivy.forum.entity.Post>()
                .eq(com.ivy.forum.entity.Post::getPostId, c.getPostId())
                .setSql("comment_count = GREATEST(comment_count - 1, 0)"));
    }
}