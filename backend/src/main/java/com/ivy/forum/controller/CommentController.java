package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Comment;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "帖子评论列表(树形)")
    @GetMapping("/{postId}")
    public Result<List<Comment>> list(@PathVariable Long postId) {
        return Result.ok(commentService.listByPost(postId));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Long> create(@RequestBody CreateCommentReq req) {
        return Result.ok(commentService.createComment(req.postId, req.parentId, req.content, SecurityUtils.currentUserId()));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id, SecurityUtils.currentUserId());
        return Result.ok();
    }

    public record CreateCommentReq(Long postId, Long parentId, String content) {}
}