package com.ivy.forum.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.entity.HotPost;
import com.ivy.forum.entity.Post;
import com.ivy.forum.mapper.HotPostMapper;
import com.ivy.forum.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotPostJob implements CommandLineRunner {

    private final PostMapper postMapper;
    private final HotPostMapper hotPostMapper;

    @Scheduled(fixedRate = 600_000)
    public void refreshHotPosts() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, "NORMAL")
                        .gt(Post::getViewCount, 0));
        List<HotPost> hot = posts.parallelStream().map(p -> {
            double hours = Duration.between(p.getCreateTime(), now).toMinutes() / 60.0;
            double decay = Math.pow(Math.max(hours, 1) + 2, 1.5);
            double score = (p.getViewCount() + p.getLikeCount() * 3.0 + p.getCommentCount() * 2.0) / decay;
            HotPost hp = new HotPost();
            hp.setPostId(p.getPostId());
            hp.setHotScore(score);
            hp.setRefreshTime(now);
            return hp;
        }).sorted((a, b) -> Double.compare(b.getHotScore(), a.getHotScore())).toList();

        hotPostMapper.delete(null);
        IntStream.range(0, Math.min(hot.size(), 100)).forEach(i -> {
            HotPost hp = hot.get(i);
            hp.setRankNo(i + 1);
            hotPostMapper.insert(hp);
        });
        log.info("hot posts refreshed, count={}", Math.min(hot.size(), 100));
    }

    @Override
    public void run(String... args) {
        refreshHotPosts();
    }
}