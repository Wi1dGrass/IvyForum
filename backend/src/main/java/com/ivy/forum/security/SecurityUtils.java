package com.ivy.forum.security;

import com.ivy.forum.common.BusinessException;
import com.ivy.forum.common.ErrorCode;
import com.ivy.forum.entity.User;
import com.ivy.forum.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static SecurityUser principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser su)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return su;
    }

    public static Long currentUserId() {
        return principal().getUserId();
    }

    public static String currentRole() {
        return principal().getRole();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(currentRole());
    }
}