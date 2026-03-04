package com.property.controller;

import com.property.dto.LoginDTO;
import com.property.dto.RegisterDTO;
import com.property.service.SysUserService;
import com.property.vo.LoginVO;
import com.property.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final SysUserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}

