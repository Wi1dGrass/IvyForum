package com.ivy.forum.service;

import com.ivy.forum.entity.Post;
import com.ivy.forum.dto.req.PostCreateRequest;
import com.ivy.forum.dto.resp.PostItemVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface PostService {
    Page<PostItemVo> listPosts(Long channelId, Long tagId, String keyword, String sort, int page, int size);
    Post detail(Long id);
    Long createPost(PostCreateRequest req, Long authorId);
    void updatePost(Long id, PostCreateRequest req, Long userId);
    void deletePost(Long id, Long userId);
    void toggleTop(Long id);
    void toggleEssence(Long id);
    List<PostItemVo> hotList();
    List<PostItemVo> userPosts(Long userId);
    List<PostItemVo> userCollects(Long userId);
}