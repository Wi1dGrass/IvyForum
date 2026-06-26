package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ivy.forum.dto.resp.TagBriefVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

    @TableField(exist = false)
    private String authorNickname;
    @TableField(exist = false)
    private String authorAvatar;
    @TableField(exist = false)
    private String channelName;
    @TableField(exist = false)
    private List<TagBriefVo> tags;
}