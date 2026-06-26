package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_post_tag")
public class PostTag {
    @TableId(type = IdType.NONE)
    private Long postId;
    private Long tagId;
}