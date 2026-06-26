package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Long tagId;
    private String name;
    private String color;
    private Integer refCount;
    private LocalDateTime createTime;
}