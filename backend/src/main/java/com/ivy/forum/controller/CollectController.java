package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.CollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "收藏")
@RestController
@RequestMapping("/collects")
@RequiredArgsConstructor
public class CollectController {

    private final CollectService collectService;

    @Operation(summary = "切换收藏")
    @PostMapping
    public Result<Map<String, Object>> toggle(@RequestBody Map<String, Long> body) {
        collectService.toggle(SecurityUtils.currentUserId(), body.get("postId"));
        boolean collected = collectService.status(SecurityUtils.currentUserId(), body.get("postId"));
        return Result.ok(Map.of("collect", collected));
    }
}