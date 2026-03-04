package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Notice;
import com.property.exception.BusinessException;
import com.property.mapper.NoticeMapper;
import com.property.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告服务实现
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    
    @Override
    public IPage<Notice> pageNotices(Integer pageNum, Integer pageSize, Integer status, String title) {
        return baseMapper.selectNoticePage(new Page<>(pageNum, pageSize), status, title);
    }
    
    @Override
    public List<Notice> getLatestNotices(Integer limit) {
        return this.list(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1) // 已发布
                .orderByDesc(Notice::getPublishTime)
                .last("LIMIT " + limit));
    }
    
    @Override
    public void addNotice(Notice notice) {
        notice.setStatus(0); // 草稿状态
        this.save(notice);
    }
    
    @Override
    public void updateNotice(Notice notice) {
        Notice existing = this.getById(notice.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        this.updateById(notice);
    }
    
    @Override
    public void publishNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        
        notice.setStatus(1);
        notice.setPublishTime(LocalDateTime.now());
        this.updateById(notice);
    }
    
    @Override
    public void deleteNotice(Long id) {
        this.removeById(id);
    }
}

