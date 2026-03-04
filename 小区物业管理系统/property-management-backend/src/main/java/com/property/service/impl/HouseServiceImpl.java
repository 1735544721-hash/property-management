package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.House;
import com.property.exception.BusinessException;
import com.property.mapper.HouseMapper;
import com.property.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 房屋服务实现
 */
@Service
@RequiredArgsConstructor
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {
    
    @Override
    public IPage<House> pageHouses(Integer pageNum, Integer pageSize, Long buildingId, Integer status) {
        return baseMapper.selectHousePage(new Page<>(pageNum, pageSize), buildingId, status);
    }
    
    @Override
    public List<House> getByOwnerId(Long ownerId) {
        // 使用自定义查询，关联楼栋表获取楼栋名称
        return baseMapper.selectByOwnerId(ownerId);
    }
    
    @Override
    public void addHouse(House house) {
        // 检查房间号是否已存在
        long count = this.count(new LambdaQueryWrapper<House>()
                .eq(House::getBuildingId, house.getBuildingId())
                .eq(House::getUnitNumber, house.getUnitNumber())
                .eq(House::getRoomNumber, house.getRoomNumber()));
        if (count > 0) {
            throw new BusinessException("该房间号已存在");
        }
        
        house.setStatus(0); // 默认空置
        this.save(house);
    }
    
    @Override
    public void updateHouse(House house) {
        // 检查房间号是否已存在（排除自身）
        long count = this.count(new LambdaQueryWrapper<House>()
                .eq(House::getBuildingId, house.getBuildingId())
                .eq(House::getUnitNumber, house.getUnitNumber())
                .eq(House::getRoomNumber, house.getRoomNumber())
                .ne(House::getId, house.getId()));
        if (count > 0) {
            throw new BusinessException("该房间号已存在");
        }
        
        this.updateById(house);
    }
    
    @Override
    public void deleteHouse(Long id) {
        House house = this.getById(id);
        if (house != null && house.getOwnerId() != null) {
            throw new BusinessException("该房屋已绑定业主，无法删除");
        }
        
        this.removeById(id);
    }
    
    @Override
    public void bindOwner(Long houseId, Long ownerId) {
        House house = this.getById(houseId);
        if (house == null) {
            throw new BusinessException("房屋不存在");
        }
        
        house.setOwnerId(ownerId);
        house.setStatus(1); // 已入住
        this.updateById(house);
    }
    
    @Override
    public void unbindOwner(Long houseId) {
        House house = this.getById(houseId);
        if (house == null) {
            throw new BusinessException("房屋不存在");
        }
        
        // 使用 LambdaUpdateWrapper 显式将 owner_id 设置为 null
        // updateById 默认不会更新 null 值字段
        LambdaUpdateWrapper<House> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(House::getId, houseId)
                .set(House::getOwnerId, null)
                .set(House::getStatus, 0);
        this.update(updateWrapper);
    }
}

