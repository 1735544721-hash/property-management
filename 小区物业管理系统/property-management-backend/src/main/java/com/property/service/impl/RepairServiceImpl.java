package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Repair;
import com.property.exception.BusinessException;
import com.property.mapper.RepairMapper;
import com.property.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 报修服务实现
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {
    
    @Override
    public IPage<Repair> pageRepairs(Integer pageNum, Integer pageSize, Long userId, Integer status) {
        return baseMapper.selectRepairPage(new Page<>(pageNum, pageSize), userId, status);
    }
    
    @Override
    public void addRepair(Repair repair) {
        repair.setStatus(0); // 待处理
        this.save(repair);
    }
    
    @Override
    public void handleRepair(Long id, Long handlerId, String handleResult, Integer status) {
        Repair repair = this.getById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        repair.setHandlerId(handlerId);
        repair.setHandleResult(handleResult);
        repair.setStatus(status);
        repair.setHandleTime(LocalDateTime.now());
        
        this.updateById(repair);
    }
    
    @Override
    public long countPending() {
        return this.count(new LambdaQueryWrapper<Repair>()
                .eq(Repair::getStatus, 0));
    }
}

