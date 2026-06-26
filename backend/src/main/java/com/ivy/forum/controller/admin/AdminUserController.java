package com.ivy.forum.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.User;
import com.ivy.forum.mapper.UserMapper;
import com.ivy.forum.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台-用户")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(summary = "用户列表")
    @GetMapping
    public Result<List<User>> list() {
        return Result.ok(userMapper.selectList(new LambdaQueryWrapper<User>()
                .select(User::getUserId, User::getUsername, User::getNickname, User::getRole, User::getStatus)
                .orderByDesc(User::getCreateTime)));
    }

    @Operation(summary = "封禁用户")
    @PostMapping("/{id}/ban")
    public Result<Void> ban(@PathVariable Long id) { userService.banUser(id); return Result.ok(); }

    @Operation(summary = "解封用户")
    @PostMapping("/{id}/unban")
    public Result<Void> unban(@PathVariable Long id) { userService.unbanUser(id); return Result.ok(); }
}