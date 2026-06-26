package com.ivy.forum.service;

import com.ivy.forum.entity.User;

public interface UserService {
    User getUserById(Long userId);
    void updateProfile(Long userId, String nickname, String avatar, String signature);
    void banUser(Long userId);
    void unbanUser(Long userId);
}