package com.ivy.forum.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.common.PageResult;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-帖子")
@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {

    private final PostMapper postMapper;
    private final PostService postService;

    @Operation(summary = "帖子列表(全部)")
    @GetMapping
    public Result<PageResult<Post>> list(@RequestParam(defaultValue = "1") long page,
                                          @RequestParam(defaultValue = "20") long size) {
        Page<Post> p = postMapper.selectPage(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>().orderByDesc(Post::getCreateTime));
        return Result.ok(PageResult.of(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @Operation(summary = "切换置顶")
    @PostMapping("/{id}/top")
    public Result<Void> top(@PathVariable Long id) { postService.toggleTop(id); return Result.ok(); }

    @Operation(summary = "切换加精")
    @PostMapping("/{id}/essence")
    public Result<Void> essence(@PathVariable Long id) { postService.toggleEssence(id); return Result.ok(); }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Post>().eq(Post::getPostId, id).setSql("status = 'DELETED'"));
        return Result.ok();
    }
}