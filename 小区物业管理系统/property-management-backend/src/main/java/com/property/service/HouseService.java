package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.entity.House;

import java.util.List;

/**
 * 房屋服务接口
 */
public interface HouseService extends IService<House> {
    
    /**
     * 分页查询房屋
     */
    IPage<House> pageHouses(Integer pageNum, Integer pageSize, Long buildingId, Integer status);
    
    /**
     * 获取业主的房屋列表
     */
    List<House> getByOwnerId(Long ownerId);
    
    /**
     * 添加房屋
     */
    void addHouse(House house);
    
    /**
     * 更新房屋
     */
    void updateHouse(House house);
    
    /**
     * 删除房屋
     */
    void deleteHouse(Long id);
    
    /**
     * 绑定业主
     */
    void bindOwner(Long houseId, Long ownerId);
    
    /**
     * 解绑业主
     */
    void unbindOwner(Long houseId);
}

