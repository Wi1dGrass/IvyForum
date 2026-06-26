package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.entity.Collect;
import com.ivy.forum.mapper.CollectMapper;
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

    @Override
    @Transactional
    public void toggle(Long userId, Long postId) {
        Collect existing = collectMapper.selectOne(
                new LambdaQueryWrapper<Collect>()
                        .eq(Collect::getUserId, userId)
                        .eq(Collect::getPostId, postId));
        if (existing != null) {
            collectMapper.deleteById(existing.getCollectId());
        } else {
            Collect c = new Collect();
            c.setUserId(userId);
            c.setPostId(postId);
            collectMapper.insert(c);
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