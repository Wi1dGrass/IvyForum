package com.ivy.forum.controller;

import com.ivy.forum.common.PageResult;
import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Notification;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.NotificationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "通知")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping
    public Result<PageResult<Notification>> list(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        Page<Notification> p = notificationService.listByUser(SecurityUtils.currentUserId(), page, size);
        return Result.ok(PageResult.of(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @Operation(summary = "未读通知数")
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        return Result.ok(notificationService.unreadCount(SecurityUtils.currentUserId()));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/read")
    public Result<Void> markRead(@RequestBody(required = false) Map<String, Long> body) {
        Long notifyId = body != null ? body.get("notifyId") : null;
        notificationService.markRead(SecurityUtils.currentUserId(), notifyId);
        return Result.ok();
    }
}