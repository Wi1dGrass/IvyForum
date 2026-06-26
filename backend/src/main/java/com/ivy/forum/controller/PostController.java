package com.ivy.forum.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.common.PageResult;
import com.ivy.forum.common.Result;
import com.ivy.forum.dto.req.PostCreateRequest;
import com.ivy.forum.entity.Post;
import com.ivy.forum.entity.PostTag;

import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.mapper.PostTagMapper;
import com.ivy.forum.mapper.TagMapper;
import com.ivy.forum.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "帖子")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostMapper postMapper;
    private final PostTagMapper postTagMapper;
    private final TagMapper tagMapper;

    @Operation(summary = "帖子列表(分页)")
    @GetMapping
    public Result<PageResult<Post>> list(
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        LambdaQueryWrapper<Post> qw = new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, "NORMAL")
                .eq(channelId != null, Post::getChannelId, channelId)
                .like(keyword != null && !keyword.isBlank(), Post::getTitle, keyword);
        if ("hot".equalsIgnoreCase(sort)) {
            qw.orderByDesc(Post::getLikeCount).orderByDesc(Post::getViewCount);
        } else {
            qw.orderByDesc(Post::getIsTop).orderByDesc(Post::getCreateTime);
        }
        Page<Post> p = postMapper.selectPage(new Page<>(page, size), qw);
        return Result.ok(PageResult.of(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @Operation(summary = "帖子详情")
    @GetMapping("/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        Post p = postMapper.selectById(id);
        if (p == null) return Result.fail(3001, "帖子不存在");
        // 浏览量 +1
        p.setViewCount(p.getViewCount() + 1);
        postMapper.updateById(p);
        return Result.ok(p);
    }

    @Operation(summary = "发帖")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PostCreateRequest req) {
        Post p = new Post();
        p.setAuthorId(SecurityUtils.currentUserId());
        p.setChannelId(req.getChannelId());
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setStatus("NORMAL");
        p.setViewCount(0);
        p.setLikeCount(0);
        p.setCommentCount(0);
        p.setCollectCount(0);
        p.setIsTop(0);
        p.setIsEssence(0);
        postMapper.insert(p);
        if (req.getTagIds() != null) {
            for (Long tid : req.getTagIds()) {
                PostTag pt = new PostTag();
                pt.setPostId(p.getPostId());
                pt.setTagId(tid);
                postTagMapper.insert(pt);
                com.ivy.forum.entity.Tag t = tagMapper.selectById(tid);
                if (t != null) {
                    t.setRefCount(t.getRefCount() + 1);
                    tagMapper.updateById(t);
                }
            }
        }
        return Result.ok(p.getPostId());
    }

    @Operation(summary = "逻辑删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Post p = postMapper.selectById(id);
        if (p == null) return Result.fail(3001, "帖子不存在");
        if (!SecurityUtils.isAdmin() && !p.getAuthorId().equals(SecurityUtils.currentUserId())) {
            return Result.fail(1004, "无权删除他人帖子");
        }
        p.setStatus("DELETED");
        postMapper.updateById(p);
        return Result.ok();
    }
}