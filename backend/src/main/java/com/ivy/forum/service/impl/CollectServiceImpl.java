package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ivy.forum.entity.Collect;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.CollectMapper;
import com.ivy.forum.mapper.PostMapper;
import com.ivy.forum.service.CollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectServiceImpl implements CollectService {

    private final CollectMapper collectMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public void toggle(Long userId, Long postId) {
        Collect existing = collectMapper.selectOne(
                new LambdaQueryWrapper<Collect>()
                        .eq(Collect::getUserId, userId)
                        .eq(Collect::getPostId, postId));
        if (existing != null) {
            collectMapper.deleteById(existing.getCollectId());
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getPostId, postId)
                    .setSql("collect_count = GREATEST(collect_count - 1, 0)"));
        } else {
            Collect c = new Collect();
            c.setUserId(userId);
            c.setPostId(postId);
            collectMapper.insert(c);
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getPostId, postId)
                    .setSql("collect_count = collect_count + 1"));
        }
    }

    @Override
    public boolean status(Long userId, Long postId) {
        return collectMapper.selectCount(
                new LambdaQueryWrapper<Collect>()
                        .eq(Collect::getUserId, userId)
                        .eq(Collect::getPostId, postId)) > 0;
    }

    @Override
    public List<Long> userCollectPostIds(Long userId) {
        return collectMapper.selectList(
                new LambdaQueryWrapper<Collect>()
                        .eq(Collect::getUserId, userId))
                .stream().map(Collect::getPostId).collect(Collectors.toList());
    }
}