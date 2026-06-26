package com.ivy.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ivy.forum.entity.Report;
import com.ivy.forum.mapper.ReportMapper;
import com.ivy.forum.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public Long createReport(Long reporterId, String targetType, Long targetId, String reasonType, String reasonText) {
        Report r = new Report();
        r.setReporterId(reporterId);
        r.setTargetType(targetType);
        r.setTargetId(targetId);
        r.setReasonType(reasonType);
        r.setReasonText(reasonText);
        r.setStatus("PENDING");
        reportMapper.insert(r);
        return r.getReportId();
    }

    @Override
    public Page<Report> listPending(int page, int size) {
        return reportMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Report>().eq(Report::getStatus, "PENDING")
                        .orderByDesc(Report::getCreateTime));
    }

    @Override
    @Transactional
    public void handle(Long reportId, Long handlerId, String status, String remark) {
        Report r = reportMapper.selectById(reportId);
        if (r == null) throw new RuntimeException("举报不存在");
        r.setStatus(status);
        r.setHandlerId(handlerId);
        r.setHandleRemark(remark);
        r.setHandleTime(LocalDateTime.now());
        reportMapper.updateById(r);
    }
}