package com.property.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.property.config.security.LoginUser;
import com.property.dto.PasswordDTO;
import com.property.entity.SysUser;
import com.property.service.SysUserService;
import com.property.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final SysUserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<SysUser> getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.success(userService.getUserInfo(loginUser.getUserId()));
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@AuthenticationPrincipal LoginUser loginUser,
                                       @Valid @RequestBody PasswordDTO dto) {
        userService.updatePassword(loginUser.getUserId(), dto);
        return Result.success();
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@AuthenticationPrincipal LoginUser loginUser,
                                       @RequestBody SysUser user) {
        user.setId(loginUser.getUserId());
        userService.updateUserInfo(user);
        return Result.success();
    }
    
    /**
     * 分页查询用户列表（管理员和物业员工）
     * 物业员工只能查看业主列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public Result<IPage<SysUser>> listUsers(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        // 物业员工只能查看业主列表
        if ("staff".equals(loginUser.getRole())) {
            role = "resident";
        }
        return Result.success(userService.pageUsers(pageNum, pageSize, role, keyword));
    }
    
    /**
     * 更新用户状态（管理员）
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
    
    /**
     * 删除用户（管理员）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
    
    /**
     * 新增用户（管理员）
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> addUser(@RequestBody SysUser user) {
        userService.addUser(user);
        return Result.success();
    }
    
    /**
     * 编辑用户（管理员）
     */
    @PutMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> editUser(@RequestBody SysUser user) {
        userService.editUser(user);
        return Result.success();
    }
    
    /**
     * 获取用户详情（管理员）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserInfo(id));
    }
    
    /**
     * 重置用户密码（管理员）
     */
    @PutMapping("/reset-password/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
}

