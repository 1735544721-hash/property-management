package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Fee;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 费用服务接口
 */
public interface FeeService extends IService<Fee> {
    
    /**
     * 分页查询费用
     */
    IPage<Fee> pageFees(Integer pageNum, Integer pageSize, Long houseId, String feeType, Integer status, String feeMonth);
    
    /**
     * 添加费用
     */
    void addFee(Fee fee);
    
    /**
     * 缴费
     */
    void payFee(Long id);
    
    /**
     * 批量缴费
     */
    void batchPayFee(Long[] ids);
    
    /**
     * 删除费用
     */
    void deleteFee(Long id);
    
    /**
     * 统计费用
     */
    Map<String, Object> statistics();
    
    /**
     * 统计业主未缴费总额
     */
    BigDecimal sumUnpaidByOwnerId(Long ownerId);
}

