package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_collect")
public class Collect {
    @TableId(type = IdType.AUTO)
    private Long collectId;
    private Long userId;
    private Long postId;
    private LocalDateTime createTime;
}