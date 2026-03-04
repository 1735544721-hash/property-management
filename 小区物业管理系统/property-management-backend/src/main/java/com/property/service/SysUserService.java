package com.property.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.property.dto.LoginDTO;
import com.property.dto.PasswordDTO;
import com.property.dto.RegisterDTO;
import com.property.entity.SysUser;
import com.property.vo.LoginVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);
    
    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordDTO dto);
    
    /**
     * 分页查询用户
     */
    IPage<SysUser> pageUsers(Integer pageNum, Integer pageSize, String role, String keyword);
    
    /**
     * 获取用户信息
     */
    SysUser getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateUserInfo(SysUser user);
    
    /**
     * 更新用户状态
     */
    void updateStatus(Long userId, Integer status);
    
    /**
     * 新增用户（管理员）
     */
    void addUser(SysUser user);
    
    /**
     * 编辑用户（管理员）
     */
    void editUser(SysUser user);
    
    /**
     * 重置密码
     */
    void resetPassword(Long userId);
}

