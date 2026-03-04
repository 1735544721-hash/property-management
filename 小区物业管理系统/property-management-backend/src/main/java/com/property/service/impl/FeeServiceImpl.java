package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Fee;
import com.property.entity.House;
import com.property.exception.BusinessException;
import com.property.mapper.FeeMapper;
import com.property.mapper.HouseMapper;
import com.property.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 费用服务实现
 */
@Service
@RequiredArgsConstructor
public class FeeServiceImpl extends ServiceImpl<FeeMapper, Fee> implements FeeService {
    
    private final HouseMapper houseMapper;
    
    @Override
    public IPage<Fee> pageFees(Integer pageNum, Integer pageSize, Long houseId, String feeType, Integer status, String feeMonth) {
        return baseMapper.selectFeePage(new Page<>(pageNum, pageSize), houseId, feeType, status, feeMonth);
    }
    
    @Override
    public void addFee(Fee fee) {
        // 检查是否已存在相同费用
        long count = this.count(new LambdaQueryWrapper<Fee>()
                .eq(Fee::getHouseId, fee.getHouseId())
                .eq(Fee::getFeeType, fee.getFeeType())
                .eq(Fee::getFeeMonth, fee.getFeeMonth()));
        if (count > 0) {
            throw new BusinessException("该房屋本月此类费用已存在");
        }
        
        fee.setStatus(0); // 未缴
        this.save(fee);
    }
    
    @Override
    public void payFee(Long id) {
        Fee fee = this.getById(id);
        if (fee == null) {
            throw new BusinessException("费用记录不存在");
        }
        
        if (fee.getStatus() == 1) {
            throw new BusinessException("该费用已缴纳");
        }
        
        fee.setStatus(1);
        fee.setPayTime(LocalDateTime.now());
        this.updateById(fee);
    }
    
    @Override
    public void batchPayFee(Long[] ids) {
        Arrays.stream(ids).forEach(this::payFee);
    }
    
    @Override
    public void deleteFee(Long id) {
        Fee fee = this.getById(id);
        if (fee != null && fee.getStatus() == 1) {
            throw new BusinessException("已缴费用不能删除");
        }
        
        this.removeById(id);
    }
    
    @Override
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 已缴费总额
        BigDecimal paidTotal = this.list(new LambdaQueryWrapper<Fee>().eq(Fee::getStatus, 1))
                .stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 未缴费总额
        BigDecimal unpaidTotal = this.list(new LambdaQueryWrapper<Fee>().eq(Fee::getStatus, 0))
                .stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 未缴费数量
        long unpaidCount = this.count(new LambdaQueryWrapper<Fee>().eq(Fee::getStatus, 0));
        
        result.put("paidTotal", paidTotal);
        result.put("unpaidTotal", unpaidTotal);
        result.put("unpaidCount", unpaidCount);
        
        return result;
    }
    
    @Override
    public BigDecimal sumUnpaidByOwnerId(Long ownerId) {
        // 获取业主的所有房屋
        List<House> houses = houseMapper.selectList(new LambdaQueryWrapper<House>()
                .eq(House::getOwnerId, ownerId));
        
        if (houses.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        List<Long> houseIds = houses.stream().map(House::getId).toList();
        
        return this.list(new LambdaQueryWrapper<Fee>()
                .in(Fee::getHouseId, houseIds)
                .eq(Fee::getStatus, 0))
                .stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

