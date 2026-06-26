package com.ivy.forum.controller.admin;

import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Channel;
import com.ivy.forum.mapper.ChannelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台-频道")
@RestController
@RequestMapping("/admin/channels")
@RequiredArgsConstructor
public class AdminChannelController {

    private final ChannelMapper channelMapper;

    @Operation(summary = "新建频道")
    @PostMapping
    public Result<Void> create(@RequestBody Channel c) { channelMapper.insert(c); return Result.ok(); }

    @Operation(summary = "编辑频道")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Channel c) { c.setChannelId(id); channelMapper.updateById(c); return Result.ok(); }

    @Operation(summary = "删除频道")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { channelMapper.deleteById(id); return Result.ok(); }
}