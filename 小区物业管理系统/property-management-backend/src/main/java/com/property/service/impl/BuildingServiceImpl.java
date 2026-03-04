package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.entity.Building;
import com.property.exception.BusinessException;
import com.property.mapper.BuildingMapper;
import com.property.mapper.HouseMapper;
import com.property.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 楼栋服务实现
 */
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {
    
    private final HouseMapper houseMapper;
    
    @Override
    public IPage<Building> pageBuildings(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Building::getBuildingName, keyword);
        }
        
        wrapper.orderByAsc(Building::getBuildingName);
        
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public List<Building> listAll() {
        return this.list(new LambdaQueryWrapper<Building>().orderByAsc(Building::getBuildingName));
    }
    
    @Override
    public void addBuilding(Building building) {
        // 检查楼栋名称是否已存在
        long count = this.count(new LambdaQueryWrapper<Building>()
                .eq(Building::getBuildingName, building.getBuildingName()));
        if (count > 0) {
            throw new BusinessException("楼栋名称已存在");
        }
        
        this.save(building);
    }
    
    @Override
    public void updateBuilding(Building building) {
        // 检查楼栋名称是否已存在（排除自身）
        long count = this.count(new LambdaQueryWrapper<Building>()
                .eq(Building::getBuildingName, building.getBuildingName())
                .ne(Building::getId, building.getId()));
        if (count > 0) {
            throw new BusinessException("楼栋名称已存在");
        }
        
        this.updateById(building);
    }
    
    @Override
    public void deleteBuilding(Long id) {
        // 检查是否有关联的房屋
        long count = houseMapper.selectCount(new LambdaQueryWrapper<com.property.entity.House>()
                .eq(com.property.entity.House::getBuildingId, id));
        if (count > 0) {
            throw new BusinessException("该楼栋下存在房屋，无法删除");
        }
        
        this.removeById(id);
    }
}

