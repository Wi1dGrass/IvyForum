package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_hot_post")
public class HotPost {
    @TableId
    private Long postId;
    private Double hotScore;
    private Integer rankNo;
    private LocalDateTime refreshTime;
}