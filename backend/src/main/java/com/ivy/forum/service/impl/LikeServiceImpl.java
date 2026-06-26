package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ivy.forum.entity.Like;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.LikeMapper;
import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public void toggle(Long userId, String targetType, Long targetId) {
        Like existing = likeMapper.selectOne(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, targetType)
                        .eq(Like::getTargetId, targetId));
        if (existing != null) {
            likeMapper.deleteById(existing.getLikeId());
            if ("POST".equals(targetType)) {
                postMapper.update(null, new LambdaUpdateWrapper<Post>()
                        .eq(Post::getPostId, targetId)
                        .setSql("like_count = GREATEST(like_count - 1, 0)"));
            }
        } else {
            Like l = new Like();
            l.setUserId(userId);
            l.setTargetType(targetType);
            l.setTargetId(targetId);
            likeMapper.insert(l);
            if ("POST".equals(targetType)) {
                postMapper.update(null, new LambdaUpdateWrapper<Post>()
                        .eq(Post::getPostId, targetId)
                        .setSql("like_count = like_count + 1"));
            }
        }
    }

    @Override
    public boolean status(Long userId, String targetType, Long targetId) {
        return likeMapper.selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetType, targetType)
                        .eq(Like::getTargetId, targetId)) > 0;
    }
}