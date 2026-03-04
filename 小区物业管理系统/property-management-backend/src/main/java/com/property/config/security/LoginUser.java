package com.property.config.security;

import lombok.Data;

/**
 * 登录用户信息
 */
@Data
public class LoginUser {
    
    private Long userId;
    private String username;
    private String role;
    
    public LoginUser(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
}

