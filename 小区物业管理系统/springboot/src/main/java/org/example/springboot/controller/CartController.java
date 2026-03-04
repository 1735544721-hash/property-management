package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.springboot.DTO.CartDTO;
import org.example.springboot.common.Result;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.CartService;
import org.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 购物车控制器（基于数据库存储）
 */
@Tag(name = "购物车接口")
@RestController
@RequestMapping("/cart")
public class CartController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);
    
    @Resource
    private CartService cartService;
    
    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 优先从request attribute获取（拦截器设置的）
        Long userId = (Long) request.getAttribute("userId");
        
        // 如果没有，尝试通过JWT工具类获取
        if (userId == null) {
            var user = JwtTokenUtils.getCurrentUser();
            if (user != null) {
                userId = user.getId();
            }
        }
        
        if (userId == null) {
            throw new ServiceException("用户未登录");
        }
        
        return userId;
    }
    
    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<?> addToCart(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        Long userId = getCurrentUserId(request);
        CartDTO cartDTO = cartService.addToCart(userId, productId, quantity);
        return Result.success(cartDTO);
    }
    
    @Operation(summary = "更新购物车商品数量")
    @PutMapping("/update")
    public Result<?> updateCartItemQuantity(
            HttpServletRequest request,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Long userId = getCurrentUserId(request);
        CartDTO cartDTO = cartService.updateCartItemQuantity(userId, productId, quantity);
        return Result.success(cartDTO);
    }
    
    @Operation(summary = "从购物车移除商品")
    @DeleteMapping("/remove")
    public Result<?> removeFromCart(
            HttpServletRequest request,
            @RequestParam Long productId) {
        Long userId = getCurrentUserId(request);
        CartDTO cartDTO = cartService.removeFromCart(userId, productId);
        return Result.success(cartDTO);
    }
    
    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<?> clearCart(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        CartDTO cartDTO = cartService.clearCart(userId);
        return Result.success(cartDTO);
    }
    
    @Operation(summary = "获取购物车")
    @GetMapping
    public Result<?> getCart(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        CartDTO cartDTO = cartService.getCart(userId);
        return Result.success(cartDTO);
    }
}
