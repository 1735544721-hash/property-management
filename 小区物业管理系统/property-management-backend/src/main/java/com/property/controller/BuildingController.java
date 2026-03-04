package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.entity.Building;
import com.property.service.BuildingService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼栋控制器
 */
@RestController
@RequestMapping("/building")
@RequiredArgsConstructor
public class BuildingController {
    
    private final BuildingService buildingService;
    
    /**
     * 分页查询楼栋
     */
    @GetMapping("/list")
    public Result<IPage<Building>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(buildingService.pageBuildings(pageNum, pageSize, keyword));
    }
    
    /**
     * 获取所有楼栋列表
     */
    @GetMapping("/all")
    public Result<List<Building>> listAll() {
        return Result.success(buildingService.listAll());
    }
    
    /**
     * 获取楼栋详情
     */
    @GetMapping("/{id}")
    public Result<Building> getById(@PathVariable Long id) {
        return Result.success(buildingService.getById(id));
    }
    
    /**
     * 添加楼栋
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> add(@RequestBody Building building) {
        buildingService.addBuilding(building);
        return Result.success();
    }
    
    /**
     * 更新楼栋
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> update(@RequestBody Building building) {
        buildingService.updateBuilding(building);
        return Result.success();
    }
    
    /**
     * 删除楼栋
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return Result.success();
    }
}

