package com.ivy.forum.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Channel;
import com.ivy.forum.mapper.ChannelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "频道")
@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelMapper channelMapper;

    @Operation(summary = "频道列表")
    @GetMapping
    public Result<List<Channel>> list() {
        return Result.ok(channelMapper.selectList(
                new LambdaQueryWrapper<Channel>().orderByAsc(Channel::getSort)));
    }

    @Operation(summary = "频道树")
    @GetMapping("/tree")
    public Result<List<Channel>> tree() {
        return Result.ok(channelMapper.selectList(
                new LambdaQueryWrapper<Channel>()
                        .eq(Channel::getStatus, "NORMAL")
                        .orderByAsc(Channel::getSort)));
    }
}