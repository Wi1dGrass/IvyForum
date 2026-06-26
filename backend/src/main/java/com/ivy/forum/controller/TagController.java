package com.ivy.forum.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Tag;
import com.ivy.forum.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "标签")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagMapper tagMapper;

    @Operation(summary = "全部标签")
    @GetMapping
    public Result<List<Tag>> list() {
        return Result.ok(tagMapper.selectList(new LambdaQueryWrapper<>()));
    }

    @Operation(summary = "热门标签")
    @GetMapping("/hot")
    public Result<List<Tag>> hot() {
        return Result.ok(tagMapper.selectList(
                new LambdaQueryWrapper<Tag>()
                        .orderByDesc(Tag::getRefCount)
                        .last("limit 20")));
    }
}