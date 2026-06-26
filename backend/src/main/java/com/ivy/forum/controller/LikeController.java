package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "点赞")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "切换点赞")
    @PostMapping
    public Result<Map<String, Object>> toggle(@RequestBody ToggleReq req) {
        likeService.toggle(SecurityUtils.currentUserId(), req.targetType, req.targetId);
        boolean liked = likeService.status(SecurityUtils.currentUserId(), req.targetType, req.targetId);
        return Result.ok(Map.of("liked", liked));
    }

    @Operation(summary = "点赞状态")
    @GetMapping("/status")
    public Result<Map<String, Boolean>> status(@RequestParam String targetType, @RequestParam Long targetId) {
        return Result.ok(Map.of("liked", likeService.status(SecurityUtils.currentUserId(), targetType, targetId)));
    }

    public record ToggleReq(String targetType, Long targetId) {}
}