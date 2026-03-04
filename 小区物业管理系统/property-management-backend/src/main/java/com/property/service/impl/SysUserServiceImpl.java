package com.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.dto.LoginDTO;
import com.property.dto.PasswordDTO;
import com.property.dto.RegisterDTO;
import com.property.entity.SysUser;
import com.property.exception.BusinessException;
import com.property.mapper.SysUserMapper;
import com.property.service.SysUserService;
import com.property.utils.JwtUtils;
import com.property.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询用户
        SysUser user = this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .token(token)
                .build();
    }
    
    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("resident"); // 默认为业主角色
        user.setStatus(1);
        
        this.save(user);
    }
    
    @Override
    public void updatePassword(Long userId, PasswordDTO dto) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
    }
    
    @Override
    public IPage<SysUser> pageUsers(Integer pageNum, Integer pageSize, String role, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(role)) {
            wrapper.eq(SysUser::getRole, role);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        
        wrapper.orderByDesc(SysUser::getId);
        
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = this.getById(userId);
        if (user != null) {
            user.setPassword(null); // 清除密码
        }
        return user;
    }
    
    @Override
    public void updateUserInfo(SysUser user) {
        // 不允许更新密码和角色
        user.setPassword(null);
        user.setRole(null);
        this.updateById(user);
    }
    
    @Override
    public void updateStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        this.updateById(user);
    }
    
    @Override
    public void addUser(SysUser user) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 设置默认密码
        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setStatus(1);
        
        this.save(user);
    }
    
    @Override
    public void editUser(SysUser user) {
        // 检查用户名是否已存在（排除自身）
        long count = this.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername())
                .ne(SysUser::getId, user.getId()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 不允许修改密码
        user.setPassword(null);
        this.updateById(user);
    }
    
    @Override
    public void resetPassword(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置为默认密码
        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        this.updateById(user);
    }
}

