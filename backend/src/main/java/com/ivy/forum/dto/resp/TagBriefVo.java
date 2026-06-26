package com.ivy.forum.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagBriefVo {
    private Long tagId;
    private String name;
    private String color;
}