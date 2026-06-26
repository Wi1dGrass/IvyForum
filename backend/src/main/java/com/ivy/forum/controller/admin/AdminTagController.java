package com.ivy.forum.controller.admin;

import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Tag;
import com.ivy.forum.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "后台-标签")
@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagMapper tagMapper;

    @Operation(summary = "新建标签")
    @PostMapping
    public Result<Void> create(@RequestBody Tag t) { tagMapper.insert(t); return Result.ok(); }

    @Operation(summary = "编辑标签")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Tag t) { t.setTagId(id); tagMapper.updateById(t); return Result.ok(); }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { tagMapper.deleteById(id); return Result.ok(); }
}