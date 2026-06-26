package com.ivy.forum.controller;

import com.ivy.forum.common.PageResult;
import com.ivy.forum.common.Result;
import com.ivy.forum.dto.req.PostCreateRequest;
import com.ivy.forum.dto.resp.PostItemVo;
import com.ivy.forum.entity.Post;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.PostService;
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

    private final PostService postService;

    @Operation(summary = "帖子列表(分页)")
    @GetMapping
    public Result<PageResult<PostItemVo>> list(
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        var p = postService.listPosts(channelId, tagId, keyword, sort, page, size);
        return Result.ok(PageResult.of(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @Operation(summary = "热门榜")
    @GetMapping("/hot")
    public Result<List<PostItemVo>> hot() {
        return Result.ok(postService.hotList());
    }

    @Operation(summary = "帖子详情")
    @GetMapping("/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        return Result.ok(postService.detail(id));
    }

    @Operation(summary = "发帖")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PostCreateRequest req) {
        return Result.ok(postService.createPost(req, SecurityUtils.currentUserId()));
    }

    @Operation(summary = "编辑帖子")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PostCreateRequest req) {
        postService.updatePost(id, req, SecurityUtils.currentUserId());
        return Result.ok();
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postService.deletePost(id, SecurityUtils.currentUserId());
        return Result.ok();
    }
}