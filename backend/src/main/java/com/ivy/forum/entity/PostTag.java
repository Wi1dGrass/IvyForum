package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_post_tag")
public class PostTag {
    private Long postId;
    private Long tagId;
}