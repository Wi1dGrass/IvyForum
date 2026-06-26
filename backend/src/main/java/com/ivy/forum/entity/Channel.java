package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_channel")
public class Channel {
    @TableId(type = IdType.AUTO)
    private Long channelId;
    private Long parentId;
    private String name;
    private Integer sort;
    private String icon;
    private String description;
    private String status;
    private LocalDateTime createTime;
}