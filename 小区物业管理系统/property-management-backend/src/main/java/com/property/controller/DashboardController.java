package com.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.entity.*;
import com.property.service.*;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final SysUserService userService;
    private final BuildingService buildingService;
    private final HouseService houseService;
    private final RepairService repairService;
    private final FeeService feeService;
    private final NoticeService noticeService;
    private final ComplaintService complaintService;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        stats.put("userCount", userService.count());
        stats.put("residentCount", userService.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "resident")));
        
        // 房产统计
        stats.put("buildingCount", buildingService.count());
        stats.put("houseCount", houseService.count());
        stats.put("occupiedHouseCount", houseService.count(new LambdaQueryWrapper<House>()
                .eq(House::getStatus, 1)));
        
        // 报修统计
        stats.put("pendingRepairCount", repairService.countPending());
        stats.put("repairCount", repairService.count());
        
        // 投诉统计
        stats.put("pendingComplaintCount", complaintService.countPending());
        
        // 费用统计
        stats.putAll(feeService.statistics());
        
        // 公告统计
        stats.put("noticeCount", noticeService.count(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1)));
        
        return Result.success(stats);
    }
}

