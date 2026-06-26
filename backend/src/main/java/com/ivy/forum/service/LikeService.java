package com.ivy.forum.service;

import com.ivy.forum.entity.Like;
import java.util.Map;

public interface LikeService {
    void toggle(Long userId, String targetType, Long targetId);
    boolean status(Long userId, String targetType, Long targetId);
}