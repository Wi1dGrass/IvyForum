package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_like")
public class Like {
    @TableId(type = IdType.AUTO)
    private Long likeId;
    private Long userId;
    private String targetType;
    private Long targetId;
    private LocalDateTime createTime;
}