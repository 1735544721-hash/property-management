package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.entity.Repair;
import com.property.service.RepairService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 报修控制器
 */
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {
    
    private final RepairService repairService;
    
    /**
     * 分页查询报修（管理员/物业）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<IPage<Repair>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(repairService.pageRepairs(pageNum, pageSize, null, status));
    }
    
    /**
     * 查询我的报修（业主）
     */
    @GetMapping("/my")
    public Result<IPage<Repair>> myRepairs(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(repairService.pageRepairs(pageNum, pageSize, loginUser.getUserId(), status));
    }
    
    /**
     * 获取报修详情
     */
    @GetMapping("/{id}")
    public Result<Repair> getById(@PathVariable Long id) {
        return Result.success(repairService.getById(id));
    }
    
    /**
     * 提交报修
     */
    @PostMapping("/add")
    public Result<Void> add(@AuthenticationPrincipal LoginUser loginUser,
                            @RequestBody Repair repair) {
        repair.setUserId(loginUser.getUserId());
        repairService.addRepair(repair);
        return Result.success();
    }
    
    /**
     * 处理报修
     */
    @PutMapping("/handle/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> handle(@AuthenticationPrincipal LoginUser loginUser,
                               @PathVariable Long id,
                               @RequestParam String handleResult,
                               @RequestParam Integer status) {
        repairService.handleRepair(id, loginUser.getUserId(), handleResult, status);
        return Result.success();
    }
    
    /**
     * 统计待处理数量
     */
    @GetMapping("/pending/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Long> countPending() {
        return Result.success(repairService.countPending());
    }
}

