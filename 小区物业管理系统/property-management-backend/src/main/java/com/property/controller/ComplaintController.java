package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.entity.Complaint;
import com.property.service.ComplaintService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 投诉建议控制器
 */
@RestController
@RequestMapping("/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    
    private final ComplaintService complaintService;
    
    /**
     * 分页查询投诉建议（管理员/物业）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<IPage<Complaint>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        return Result.success(complaintService.pageComplaints(pageNum, pageSize, null, type, status));
    }
    
    /**
     * 查询我的投诉建议（业主）
     */
    @GetMapping("/my")
    public Result<IPage<Complaint>> myComplaints(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        return Result.success(complaintService.pageComplaints(pageNum, pageSize, loginUser.getUserId(), type, status));
    }
    
    /**
     * 获取投诉建议详情
     */
    @GetMapping("/{id}")
    public Result<Complaint> getById(@PathVariable Long id) {
        return Result.success(complaintService.getById(id));
    }
    
    /**
     * 提交投诉建议
     */
    @PostMapping("/add")
    public Result<Void> add(@AuthenticationPrincipal LoginUser loginUser,
                            @RequestBody Complaint complaint) {
        complaint.setUserId(loginUser.getUserId());
        complaintService.addComplaint(complaint);
        return Result.success();
    }
    
    /**
     * 处理投诉建议
     */
    @PutMapping("/handle/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> handle(@AuthenticationPrincipal LoginUser loginUser,
                               @PathVariable Long id,
                               @RequestParam String reply) {
        complaintService.handleComplaint(id, loginUser.getUserId(), reply);
        return Result.success();
    }
    
    /**
     * 统计待处理数量
     */
    @GetMapping("/pending/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Long> countPending() {
        return Result.success(complaintService.countPending());
    }
}

