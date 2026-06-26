package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.dto.req.LoginRequest;
import com.ivy.forum.dto.req.RegisterRequest;
import com.ivy.forum.dto.resp.LoginResponse;
import com.ivy.forum.entity.User;
import com.ivy.forum.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.ok(authService.register(req));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @Operation(summary = "当前用户")
    @GetMapping("/me")
    public Result<User> me() {
        return Result.ok(authService.currentUser());
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }
}