package com.ivy.forum.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityUser {
    private final Long userId;
    private final String username;
    private final String role;
}