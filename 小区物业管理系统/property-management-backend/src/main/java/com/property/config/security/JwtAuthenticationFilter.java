package com.property.config.security;

import com.property.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtils jwtUtils;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取Token
        String token = getTokenFromRequest(request);
        
        // 验证Token
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 解析Token获取用户信息
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);
            String role = jwtUtils.getRoleFromToken(token);
            
            // 创建认证对象
            // 注意：hasAnyRole('STAFF') 会自动添加 ROLE_ 前缀检查 ROLE_STAFF
            // 所以这里需要确保权限名称为 ROLE_ADMIN, ROLE_STAFF, ROLE_RESIDENT
            String authority = "ROLE_" + role.toUpperCase();
            LoginUser loginUser = new LoginUser(userId, username, role);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginUser, 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority(authority))
            );
            
            // 设置到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

