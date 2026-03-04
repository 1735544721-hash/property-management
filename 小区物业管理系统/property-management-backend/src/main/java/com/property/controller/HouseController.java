package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.entity.House;
import com.property.service.HouseService;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋控制器
 */
@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {
    
    private final HouseService houseService;
    
    /**
     * 分页查询房屋
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<IPage<House>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status) {
        return Result.success(houseService.pageHouses(pageNum, pageSize, buildingId, status));
    }
    
    /**
     * 获取我的房产
     */
    @GetMapping("/my")
    public Result<List<House>> myHouses(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(houseService.getByOwnerId(loginUser.getUserId()));
    }
    
    /**
     * 获取房屋详情
     */
    @GetMapping("/{id}")
    public Result<House> getById(@PathVariable Long id) {
        return Result.success(houseService.getById(id));
    }
    
    /**
     * 添加房屋
     */
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> add(@RequestBody House house) {
        houseService.addHouse(house);
        return Result.success();
    }
    
    /**
     * 更新房屋
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> update(@RequestBody House house) {
        houseService.updateHouse(house);
        return Result.success();
    }
    
    /**
     * 删除房屋
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return Result.success();
    }
    
    /**
     * 绑定业主
     */
    @PutMapping("/bind/{houseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> bindOwner(@PathVariable Long houseId, @RequestParam Long ownerId) {
        houseService.bindOwner(houseId, ownerId);
        return Result.success();
    }
    
    /**
     * 解绑业主
     */
    @PutMapping("/unbind/{houseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<Void> unbindOwner(@PathVariable Long houseId) {
        houseService.unbindOwner(houseId);
        return Result.success();
    }
}

