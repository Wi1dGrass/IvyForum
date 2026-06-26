package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long postId;
    private Long authorId;
    private Long channelId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer isTop;
    private Integer isEssence;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}