package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Notice;

import java.util.List;

/**
 * 公告服务接口
 */
public interface NoticeService extends IService<Notice> {
    
    /**
     * 分页查询公告
     */
    IPage<Notice> pageNotices(Integer pageNum, Integer pageSize, Integer status, String title);
    
    /**
     * 获取最新公告
     */
    List<Notice> getLatestNotices(Integer limit);
    
    /**
     * 添加公告
     */
    void addNotice(Notice notice);
    
    /**
     * 更新公告
     */
    void updateNotice(Notice notice);
    
    /**
     * 发布公告
     */
    void publishNotice(Long id);
    
    /**
     * 删除公告
     */
    void deleteNotice(Long id);
}

