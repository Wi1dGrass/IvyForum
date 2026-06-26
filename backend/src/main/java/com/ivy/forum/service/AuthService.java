package com.ivy.forum.service;

import com.ivy.forum.dto.req.LoginRequest;
import com.ivy.forum.dto.req.RegisterRequest;
import com.ivy.forum.dto.resp.LoginResponse;

public interface AuthService {
    LoginResponse register(RegisterRequest req);
    LoginResponse login(LoginRequest req);
    com.ivy.forum.entity.User currentUser();
}