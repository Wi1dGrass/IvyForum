package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long notifyId;
    private Long receiverId;
    private Long senderId;
    private String type;
    private String targetType;
    private Long targetId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}