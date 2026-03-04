package com.property.controller;

import com.property.config.security.LoginUser;
import com.property.vo.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试控制器（调试用）
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    /**
     * 检查当前用户的认证信息和权限
     */
    @GetMapping("/auth-info")
    public Result<Map<String, Object>> getAuthInfo(@AuthenticationPrincipal LoginUser loginUser) {
        Map<String, Object> info = new HashMap<>();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            info.put("authenticated", auth.isAuthenticated());
            info.put("principal", auth.getPrincipal().toString());
            info.put("authorities", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }
        
        if (loginUser != null) {
            info.put("userId", loginUser.getUserId());
            info.put("username", loginUser.getUsername());
            info.put("role", loginUser.getRole());
        }
        
        return Result.success(info);
    }
}

