package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Repair;

/**
 * 报修服务接口
 */
public interface RepairService extends IService<Repair> {
    
    /**
     * 分页查询报修
     */
    IPage<Repair> pageRepairs(Integer pageNum, Integer pageSize, Long userId, Integer status);
    
    /**
     * 提交报修
     */
    void addRepair(Repair repair);
    
    /**
     * 处理报修
     */
    void handleRepair(Long id, Long handlerId, String handleResult, Integer status);
    
    /**
     * 统计待处理报修数量
     */
    long countPending();
}

