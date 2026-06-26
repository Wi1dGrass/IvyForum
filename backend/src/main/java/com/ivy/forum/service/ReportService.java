package com.ivy.forum.service;

import com.ivy.forum.entity.Report;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ReportService {
    Long createReport(Long reporterId, String targetType, Long targetId, String reasonType, String reasonText);
    Page<Report> listPending(int page, int size);
    void handle(Long reportId, Long handlerId, String status, String remark);
}