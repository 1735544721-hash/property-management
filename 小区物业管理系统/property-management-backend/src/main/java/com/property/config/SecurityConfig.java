package com.property.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.config.security.JwtAuthenticationFilter;
import com.property.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        
        http
            // 禁用CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 允许跨域预检请求
            .cors(cors -> {})
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 放行登录、注册接口
                .requestMatchers("/auth/**").permitAll()
                // 放行静态资源
                .requestMatchers("/static/**", "/images/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 异常处理
            .exceptionHandling(ex -> ex
                // 未认证处理
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(401);
                    response.getWriter().write(objectMapper.writeValueAsString(
                        Result.unauthorized("未登录或登录已过期")
                    ));
                })
                // 权限不足处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(403);
                    response.getWriter().write(objectMapper.writeValueAsString(
                        Result.forbidden("权限不足，无法访问")
                    ));
                })
            )
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

