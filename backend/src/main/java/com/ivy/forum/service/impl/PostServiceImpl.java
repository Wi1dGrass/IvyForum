package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.common.BusinessException;
import com.ivy.forum.common.ErrorCode;
import com.ivy.forum.dto.req.PostCreateRequest;
import com.ivy.forum.dto.resp.PostItemVo;
import com.ivy.forum.dto.resp.TagBriefVo;
import com.ivy.forum.entity.*;
import com.ivy.forum.mapper.*;
import java.util.*;
import java.util.stream.Collectors;
import com.ivy.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostTagMapper postTagMapper;
    private final TagMapper tagMapper;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;
    private final CollectMapper collectMapper;
    private final HotPostMapper hotPostMapper;

    @Override
    public Page<PostItemVo> listPosts(Long channelId, Long tagId, String keyword, String sort, int page, int size) {
        LambdaQueryWrapper<Post> qw = new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, "NORMAL")
                .eq(channelId != null && channelId > 0, Post::getChannelId, channelId)
                .like(keyword != null && !keyword.isBlank(), Post::getTitle, keyword)
                .orderByDesc(Post::getIsTop);

        if ("hot".equalsIgnoreCase(sort)) {
            qw.orderByDesc(Post::getLikeCount).orderByDesc(Post::getViewCount).orderByDesc(Post::getCreateTime);
        } else {
            qw.orderByDesc(Post::getCreateTime);
        }

        Page<Post> p = postMapper.selectPage(new Page<>(page, size), qw);

        if (tagId != null && tagId > 0 && !p.getRecords().isEmpty()) {
            List<Long> postIds = p.getRecords().stream().map(Post::getPostId).collect(Collectors.toList());
            Set<Long> filtered = postTagMapper.selectList(
                    new LambdaQueryWrapper<PostTag>().in(PostTag::getPostId, postIds).eq(PostTag::getTagId, tagId)
            ).stream().map(PostTag::getPostId).collect(Collectors.toSet());
            p.setRecords(p.getRecords().stream().filter(r -> filtered.contains(r.getPostId())).collect(Collectors.toList()));
            p.setTotal(p.getRecords().size());
        }

        List<PostItemVo> vos = p.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        Page<PostItemVo> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        result.setRecords(vos);
        return result;
    }

    @Override
    public Post detail(Long id) {
        Post p = postMapper.selectById(id);
        if (p == null || "DELETED".equals(p.getStatus())) throw new BusinessException(ErrorCode.NOT_FOUND);
        p.setViewCount(p.getViewCount() + 1);
        postMapper.updateById(p);
        // populate extra fields
        User author = userMapper.selectById(p.getAuthorId());
        if (author != null) {
            p.setAuthorNickname(author.getNickname());
            p.setAuthorAvatar(author.getAvatar());
        }
        Channel channel = channelMapper.selectById(p.getChannelId());
        if (channel != null) {
            p.setChannelName(channel.getName());
        }
        List<PostTag> pts = postTagMapper.selectList(
                new LambdaQueryWrapper<PostTag>().eq(PostTag::getPostId, id));
        List<TagBriefVo> tags = pts.stream()
                .map(pt -> tagMapper.selectById(pt.getTagId()))
                .filter(Objects::nonNull)
                .map(t -> new TagBriefVo(t.getTagId(), t.getName(), t.getColor()))
                .collect(Collectors.toList());
        p.setTags(tags);
        return p;
    }

    @Override
    @Transactional
    public Long createPost(PostCreateRequest req, Long authorId) {
        Post p = new Post();
        p.setAuthorId(authorId);
        p.setChannelId(req.getChannelId());
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setStatus("NORMAL");
        postMapper.insert(p);
        if (req.getTagIds() != null) {
            for (Long tid : req.getTagIds()) {
                PostTag pt = new PostTag();
                pt.setPostId(p.getPostId());
                pt.setTagId(tid);
                postTagMapper.insert(pt);
                tagMapper.update(null, new LambdaUpdateWrapper<Tag>().eq(Tag::getTagId, tid)
                        .setSql("ref_count = ref_count + 1"));
            }
        }
        return p.getPostId();
    }

    @Override
    @Transactional
    public void updatePost(Long id, PostCreateRequest req, Long userId) {
        Post p = postMapper.selectById(id);
        if (p == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        if (!p.getAuthorId().equals(userId) && !com.ivy.forum.security.SecurityUtils.isAdmin())
            throw new BusinessException(ErrorCode.FORBIDDEN);
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setChannelId(req.getChannelId());
        postMapper.updateById(p);
        postTagMapper.delete(new LambdaQueryWrapper<PostTag>().eq(PostTag::getPostId, id));
        if (req.getTagIds() != null) {
            for (Long tid : req.getTagIds()) {
                PostTag pt = new PostTag();
                pt.setPostId(id);
                pt.setTagId(tid);
                postTagMapper.insert(pt);
            }
        }
    }

    @Override
    public void deletePost(Long id, Long userId) {
        Post p = postMapper.selectById(id);
        if (p == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        if (!p.getAuthorId().equals(userId) && !com.ivy.forum.security.SecurityUtils.isAdmin())
            throw new BusinessException(ErrorCode.FORBIDDEN);
        p.setStatus("DELETED");
        postMapper.updateById(p);
    }

    @Override
    public void toggleTop(Long id) {
        Post p = postMapper.selectById(id);
        if (p == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        p.setIsTop(p.getIsTop() == 1 ? 0 : 1);
        postMapper.updateById(p);
    }

    @Override
    public void toggleEssence(Long id) {
        Post p = postMapper.selectById(id);
        if (p == null) throw new BusinessException(ErrorCode.NOT_FOUND);
        p.setIsEssence(p.getIsEssence() == 1 ? 0 : 1);
        postMapper.updateById(p);
    }

    @Override
    public List<PostItemVo> hotList() {
        List<HotPost> hotPosts = hotPostMapper.selectList(
                new LambdaQueryWrapper<HotPost>().orderByAsc(HotPost::getRankNo));
        if (hotPosts.isEmpty()) return Collections.emptyList();
        Map<Long, HotPost> hotMap = hotPosts.stream().collect(Collectors.toMap(HotPost::getPostId, hp -> hp));
        List<Long> ids = hotPosts.stream().map(HotPost::getPostId).collect(Collectors.toList());
        Map<Long, Post> postMap = postMapper.selectList(
                new LambdaQueryWrapper<Post>().in(Post::getPostId, ids))
                .stream().collect(Collectors.toMap(Post::getPostId, p -> p));
        return ids.stream().map(id -> {
            Post p = postMap.get(id);
            if (p == null) return null;
            PostItemVo vo = toVo(p);
            HotPost hp = hotMap.get(id);
            if (hp != null) {
                vo.setRankNo(hp.getRankNo());
                vo.setHotScore(hp.getHotScore());
            }
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<PostItemVo> userPosts(Long userId) {
        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>().eq(Post::getAuthorId, userId).eq(Post::getStatus, "NORMAL")
                        .orderByDesc(Post::getCreateTime));
        return posts.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public List<PostItemVo> userCollects(Long userId) {
        List<Collect> collects = collectMapper.selectList(
                new LambdaQueryWrapper<Collect>().eq(Collect::getUserId, userId).orderByDesc(Collect::getCreateTime));
        if (collects.isEmpty()) return Collections.emptyList();
        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .in(Post::getPostId, collects.stream().map(Collect::getPostId).collect(Collectors.toList())));
        Map<Long, Post> map = posts.stream().collect(Collectors.toMap(Post::getPostId, p -> p));
        return collects.stream().map(c -> map.get(c.getPostId())).filter(Objects::nonNull).map(this::toVo).collect(Collectors.toList());
    }

    private PostItemVo toVo(Post p) {
        PostItemVo vo = new PostItemVo();
        vo.setPostId(p.getPostId());
        vo.setAuthorId(p.getAuthorId());
        User u = userMapper.selectById(p.getAuthorId());
        if (u != null) {
            vo.setAuthorNickname(u.getNickname());
            vo.setAuthorAvatar(u.getAvatar());
        }
        vo.setChannelId(p.getChannelId());
        Channel c = channelMapper.selectById(p.getChannelId());
        if (c != null) vo.setChannelName(c.getName());
        vo.setTitle(p.getTitle());
        vo.setContentAbstract(extractAbstract(p.getContent()));
        vo.setFirstImage(extractFirstImage(p.getContent()));
        vo.setViewCount(p.getViewCount());
        vo.setLikeCount(p.getLikeCount());
        vo.setCommentCount(p.getCommentCount());
        vo.setCollectCount(p.getCollectCount());
        vo.setIsTop(p.getIsTop());
        vo.setIsEssence(p.getIsEssence());
        vo.setCreateTime(p.getCreateTime());

        List<PostTag> pts = postTagMapper.selectList(
                new LambdaQueryWrapper<PostTag>().eq(PostTag::getPostId, p.getPostId()));
        if (!pts.isEmpty()) {
            List<Tag> tags = tagMapper.selectList(
                    new LambdaQueryWrapper<Tag>().in(Tag::getTagId,
                            pts.stream().map(PostTag::getTagId).collect(Collectors.toList())));
            vo.setTags(tags.stream().map(t -> {
                TagBriefVo tv = new TagBriefVo();
                tv.setTagId(t.getTagId());
                tv.setName(t.getName());
                tv.setColor(t.getColor());
                return tv;
            }).collect(Collectors.toList()));
        }
        return vo;
    }

    private String extractAbstract(String content) {
        if (content == null) return "";
        String text = content
                .replaceAll("!\\[.*?\\]\\(.*?\\)", " ")  // images
                .replaceAll("```[\\s\\S]*?```", " ")      // code blocks
                .replaceAll("#+", " ")                     // headings
                .replaceAll("[*_`>|\\-\\[\\]]", " ")      // markdown symbols
                .replaceAll("\\s+", " ").trim();
        return text.length() > 150 ? text.substring(0, 150) + "…" : text;
    }

    private String extractFirstImage(String content) {
        if (content == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("!\\[.*?\\]\\((.*?)\\)").matcher(content);
        return m.find() ? m.group(1) : null;
    }
}