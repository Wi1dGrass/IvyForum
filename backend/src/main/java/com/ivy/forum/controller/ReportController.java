package com.ivy.forum.controller;

import com.ivy.forum.common.Result;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "举报")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "提交举报")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ReportReq req) {
        return Result.ok(reportService.createReport(
                SecurityUtils.currentUserId(), req.targetType, req.targetId, req.reasonType, req.reasonText));
    }

    public record ReportReq(@NotBlank String targetType, @NotNull Long targetId,
                             @NotBlank String reasonType, String reasonText) {}
}