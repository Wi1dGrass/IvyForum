package com.ivy.forum.service;

import java.util.List;

public interface CollectService {
    void toggle(Long userId, Long postId);
    boolean status(Long userId, Long postId);
    List<Long> userCollectPostIds(Long userId);
}