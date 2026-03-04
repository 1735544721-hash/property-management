package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Complaint;
import com.property.exception.BusinessException;
import com.property.mapper.ComplaintMapper;
import com.property.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 投诉建议服务实现
 */
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
    
    @Override
    public IPage<Complaint> pageComplaints(Integer pageNum, Integer pageSize, Long userId, String type, Integer status) {
        return baseMapper.selectComplaintPage(new Page<>(pageNum, pageSize), userId, type, status);
    }
    
    @Override
    public void addComplaint(Complaint complaint) {
        complaint.setStatus(0); // 待处理
        this.save(complaint);
    }
    
    @Override
    public void handleComplaint(Long id, Long handlerId, String reply) {
        Complaint complaint = this.getById(id);
        if (complaint == null) {
            throw new BusinessException("投诉建议不存在");
        }
        
        complaint.setHandlerId(handlerId);
        complaint.setReply(reply);
        complaint.setStatus(1); // 已处理
        complaint.setHandleTime(LocalDateTime.now());
        
        this.updateById(complaint);
    }
    
    @Override
    public long countPending() {
        return this.count(new LambdaQueryWrapper<Complaint>()
                .eq(Complaint::getStatus, 0));
    }
}

