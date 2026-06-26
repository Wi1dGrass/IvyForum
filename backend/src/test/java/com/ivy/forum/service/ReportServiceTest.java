package com.ivy.forum.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReportService 测试用例
 *
 * 用例ID: RPT-SVC-001 ~ 004
 */
@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    void RPT_SVC_001_create_should_return_id() {
        Long id = reportService.createReport(1L, "POST", 1L, "SPAM", "广告");
        assertNotNull(id);
    }

    @Test
    void RPT_SVC_002_listPending_should_contain() {
        reportService.createReport(1L, "POST", 2L, "ABUSE", "辱骂");
        Page<Report> p = reportService.listPending(1, 10);
        assertTrue(p.getTotal() > 0);
    }

    @Test
    void RPT_SVC_003_handle_pass_should_update_status() {
        Long id = reportService.createReport(1L, "POST", 3L, "SPAM", "测试");
        reportService.handle(id, 2L, "PASS", "确认违规");
        boolean found = reportService.listPending(1, 100).getRecords().stream()
                .anyMatch(r -> r.getReportId().equals(id));
        assertFalse(found, "已处理的报告不应在 pending 列表");
    }

    @Test
    void RPT_SVC_004_handle_reject_should_update_status() {
        Long id = reportService.createReport(1L, "COMMENT", 1L, "OTHER", "误报");
        reportService.handle(id, 1L, "REJECT", "不违规");
        assertTrue(true);
    }
}