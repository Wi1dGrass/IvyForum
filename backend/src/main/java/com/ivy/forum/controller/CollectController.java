package com.ivy.forum.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Collect;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.CollectMapper;
import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.CollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "收藏")
@RestController
@RequestMapping("/collects")
@RequiredArgsConstructor
public class CollectController {

    private final CollectService collectService;
    private final CollectMapper collectMapper;
    private final PostMapper postMapper;

    @Operation(summary = "切换收藏")
    @PostMapping
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body) {
        collectService.toggle(SecurityUtils.currentUserId(), body.get("postId"));
        boolean collected = collectService.status(SecurityUtils.currentUserId(), body.get("postId"));
        return Result.ok(Map.of("collected", collected));
    }

    @Operation(summary = "收藏列表")
    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        Long userId = SecurityUtils.currentUserId();
        List<Collect> records = collectMapper.selectList(
                new LambdaQueryWrapper<Collect>().eq(Collect::getUserId, userId));
        List<Map<String, Object>> items = new ArrayList<>();
        for (Collect c : records) {
            Post p = postMapper.selectById(c.getPostId());
            items.add(Map.of(
                    "collectId", c.getCollectId(),
                    "postId", c.getPostId(),
                    "title", p != null ? p.getTitle() : "",
                    "createTime", c.getCreateTime() != null ? c.getCreateTime().toString() : ""
            ));
        }
        return Result.ok(items);
    }
}