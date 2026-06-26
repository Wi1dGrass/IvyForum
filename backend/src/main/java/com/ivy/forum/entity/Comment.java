package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long commentId;
    private Long postId;
    private Long parentId;
    private Long rootId;
    private Long authorId;
    private String content;
    private Integer likeCount;
    private Integer floor;
    private Integer isDeleted;
    private LocalDateTime createTime;
    @TableField(exist = false)
    private List<Comment> children;
    @TableField(exist = false)
    private String authorNickname;
    @TableField(exist = false)
    private String authorAvatar;
    @TableField(exist = false)
    private String authorRole;
}