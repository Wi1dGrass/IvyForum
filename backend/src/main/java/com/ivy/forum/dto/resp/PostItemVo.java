package com.ivy.forum.dto.resp;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostItemVo {
    private Long postId;
    private Long authorId;
    private String authorNickname;
    private String authorAvatar;
    private Long channelId;
    private String channelName;
    private String title;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer isTop;
    private Integer isEssence;
    private List<TagBriefVo> tags;
    private LocalDateTime createTime;
}