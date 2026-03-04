package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.entity.Notice;
import com.property.service.NoticeService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    
    /**
     * 分页查询公告
     */
    @GetMapping("/list")
    public Result<IPage<Notice>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String title) {
        return Result.success(noticeService.pageNotices(pageNum, pageSize, status, title));
    }
    
    /**
     * 获取最新公告
     */
    @GetMapping("/latest")
    public Result<List<Notice>> latest(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(noticeService.getLatestNotices(limit));
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }
    
    /**
     * 添加公告
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> add(@AuthenticationPrincipal LoginUser loginUser,
                            @RequestBody Notice notice) {
        notice.setPublisherId(loginUser.getUserId());
        noticeService.addNotice(notice);
        return Result.success();
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> update(@RequestBody Notice notice) {
        noticeService.updateNotice(notice);
        return Result.success();
    }
    
    /**
     * 发布公告
     */
    @PutMapping("/publish/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> publish(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.success();
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
}

