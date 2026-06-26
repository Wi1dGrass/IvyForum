package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.common.BusinessException;
import com.ivy.forum.common.ErrorCode;
import com.ivy.forum.dto.req.LoginRequest;
import com.ivy.forum.dto.req.RegisterRequest;
import com.ivy.forum.dto.resp.LoginResponse;
import com.ivy.forum.entity.User;
import com.ivy.forum.mapper.UserMapper;
import com.ivy.forum.security.JwtUtil;
import com.ivy.forum.security.SecurityUser;
import com.ivy.forum.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse register(RegisterRequest req) {
        Long exist = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exist != null && exist > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setNickname(req.getNickname());
        u.setEmail(req.getEmail());
        u.setRole("STUDENT");
        u.setStatus("NORMAL");
        userMapper.insert(u);

        return buildLogin(u);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User u = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, principal.getUsername()));
        if (u == null) throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        if ("BANNED".equalsIgnoreCase(u.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        return buildLogin(u);
    }

    @Override
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser su)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User u = userMapper.selectById(su.getUserId());
        if (u == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        return u;
    }

    private LoginResponse buildLogin(User u) {
        String token = jwtUtil.generate(u.getUserId(), u.getUsername(), u.getRole());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(u.getUserId());
        resp.setUsername(u.getUsername());
        resp.setNickname(u.getNickname());
        resp.setRole(u.getRole());
        return resp;
    }
}