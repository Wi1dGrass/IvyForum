package com.ivy.forum.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostCreateRequest {
    @NotNull
    private Long channelId;
    @NotBlank
    @Size(max = 128)
    private String title;
    @NotBlank
    private String content;
    @Size(max = 5)
    private List<Long> tagIds;
}