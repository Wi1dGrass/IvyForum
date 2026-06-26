package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.dto.resp.PostItemVo;
import com.ivy.forum.entity.User;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.PostService;
import com.ivy.forum.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "用户")
@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final PostService postService;

    @Operation(summary = "用户主页")
    @GetMapping("/users/{id}/profile")
    public Result<User> profile(@PathVariable Long id) {
        return Result.ok(userService.getUserById(id));
    }

    @Operation(summary = "用户发帖")
    @GetMapping("/users/{id}/posts")
    public Result<List<PostItemVo>> userPosts(@PathVariable Long id) {
        return Result.ok(postService.userPosts(id));
    }

    @Operation(summary = "用户收藏")
    @GetMapping("/users/{id}/collects")
    public Result<List<PostItemVo>> userCollects(@PathVariable Long id) {
        return Result.ok(postService.userCollects(id));
    }

    @Operation(summary = "修改个人资料")
    @PutMapping("/user/profile")
    public Result<Void> updateProfile(@RequestBody Map<String, String> body) {
        userService.updateProfile(SecurityUtils.currentUserId(),
                body.get("nickname"), body.get("avatar"), body.get("signature"));
        return Result.ok();
    }
}