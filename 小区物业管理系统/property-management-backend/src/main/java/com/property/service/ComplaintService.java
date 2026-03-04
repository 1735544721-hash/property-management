package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Complaint;

/**
 * 投诉建议服务接口
 */
public interface ComplaintService extends IService<Complaint> {
    
    /**
     * 分页查询投诉建议
     */
    IPage<Complaint> pageComplaints(Integer pageNum, Integer pageSize, Long userId, String type, Integer status);
    
    /**
     * 提交投诉建议
     */
    void addComplaint(Complaint complaint);
    
    /**
     * 处理投诉建议
     */
    void handleComplaint(Long id, Long handlerId, String reply);
    
    /**
     * 统计待处理数量
     */
    long countPending();
}

