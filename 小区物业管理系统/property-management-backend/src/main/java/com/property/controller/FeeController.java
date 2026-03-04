package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.entity.Fee;
import com.property.entity.House;
import com.property.service.FeeService;
import com.property.service.HouseService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 费用控制器
 */
@RestController
@RequestMapping("/fee")
@RequiredArgsConstructor
public class FeeController {
    
    private final FeeService feeService;
    private final HouseService houseService;
    
    /**
     * 分页查询费用（管理员/物业）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<IPage<Fee>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long houseId,
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String feeMonth) {
        return Result.success(feeService.pageFees(pageNum, pageSize, houseId, feeType, status, feeMonth));
    }
    
    /**
     * 查询我的费用（业主）
     */
    @GetMapping("/my")
    public Result<IPage<Fee>> myFees(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String feeMonth) {
        // 获取业主的房屋
        List<House> houses = houseService.getByOwnerId(loginUser.getUserId());
        if (houses.isEmpty()) {
            return Result.success(null);
        }
        // 查询第一套房屋的费用（简化处理）
        Long houseId = houses.get(0).getId();
        return Result.success(feeService.pageFees(pageNum, pageSize, houseId, feeType, status, feeMonth));
    }
    
    /**
     * 添加费用
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> add(@RequestBody Fee fee) {
        feeService.addFee(fee);
        return Result.success();
    }
    
    /**
     * 缴费
     */
    @PutMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Long id) {
        feeService.payFee(id);
        return Result.success();
    }
    
    /**
     * 批量缴费
     */
    @PutMapping("/pay/batch")
    public Result<Void> batchPay(@RequestBody Long[] ids) {
        feeService.batchPayFee(ids);
        return Result.success();
    }
    
    /**
     * 删除费用
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        feeService.deleteFee(id);
        return Result.success();
    }
    
    /**
     * 费用统计
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Map<String, Object>> statistics() {
        return Result.success(feeService.statistics());
    }
    
    /**
     * 我的未缴费总额
     */
    @GetMapping("/my/unpaid")
    public Result<BigDecimal> myUnpaid(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(feeService.sumUnpaidByOwnerId(loginUser.getUserId()));
    }
}

