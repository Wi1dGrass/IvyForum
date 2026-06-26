package com.ivy.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_report")
public class Report {
    @TableId(type = IdType.AUTO)
    private Long reportId;
    private Long reporterId;
    private String targetType;
    private Long targetId;
    private String reasonType;
    private String reasonText;
    private String status;
    private Long handlerId;
    private String handleRemark;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}