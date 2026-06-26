package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ivy.forum.common.BusinessException;
import com.ivy.forum.common.ErrorCode;
import com.ivy.forum.entity.User;
import com.ivy.forum.mapper.UserMapper;
import com.ivy.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User getUserById(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        return u;
    }

    @Override
    public void updateProfile(Long userId, String nickname, String avatar, String signature) {
        User u = userMapper.selectById(userId);
        if (u == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        if (nickname != null) u.setNickname(nickname);
        if (avatar != null) u.setAvatar(avatar);
        if (signature != null) u.setSignature(signature);
        userMapper.updateById(u);
    }

    @Override
    public void banUser(Long userId) {
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .setSql("status = 'BANNED'"));
    }

    @Override
    public void unbanUser(Long userId) {
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .setSql("status = 'NORMAL'"));
    }
}