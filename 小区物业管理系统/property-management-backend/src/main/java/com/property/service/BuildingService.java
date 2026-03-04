package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.Building;

import java.util.List;

/**
 * 楼栋服务接口
 */
public interface BuildingService extends IService<Building> {
    
    /**
     * 分页查询楼栋
     */
    IPage<Building> pageBuildings(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 获取所有楼栋列表
     */
    List<Building> listAll();
    
    /**
     * 添加楼栋
     */
    void addBuilding(Building building);
    
    /**
     * 更新楼栋
     */
    void updateBuilding(Building building);
    
    /**
     * 删除楼栋
     */
    void deleteBuilding(Long id);
}

