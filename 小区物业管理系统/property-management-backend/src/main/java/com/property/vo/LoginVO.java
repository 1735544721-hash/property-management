package com.property.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应VO
 */
@Data
@Builder
public class LoginVO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * Token
     */
    private String token;
}

