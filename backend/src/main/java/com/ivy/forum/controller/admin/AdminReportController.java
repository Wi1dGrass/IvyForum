package com.ivy.forum.controller.admin;

import com.ivy.forum.common.Result;
import com.ivy.forum.entity.Report;
import com.ivy.forum.security.SecurityUtils;
import com.ivy.forum.service.ReportService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "后台-举报")
@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    @Operation(summary = "举报列表")
    @GetMapping
    public Result<List<Report>> list(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "50") int size) {
        Page<Report> p = reportService.listPending(page, size);
        return Result.ok(p.getRecords());
    }

    @Operation(summary = "处理举报")
    @PostMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id, @RequestBody Map<String, String> body) {
        reportService.handle(id, SecurityUtils.currentUserId(), body.get("status"), body.get("remark"));
        return Result.ok();
    }
}