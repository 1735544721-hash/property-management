package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.springboot.DTO.CartDTO;
import org.example.springboot.DTO.CartItemDTO;
import org.example.springboot.entity.CartItem;
import org.example.springboot.entity.Product;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.CartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车服务实现类（简化版：单表设计，只存储商品ID）
 */
@Service
public class CartService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);
    
    @Resource
    private CartItemMapper cartItemMapper;
    
    @Resource
    private ProductService productService;
    
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 购物车DTO
     */
    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        if (userId == null) {
            throw new ServiceException("用户ID不能为空");
        }
        if (quantity <= 0) {
            throw new ServiceException("商品数量必须大于0");
        }
        
        // 获取商品信息（验证商品是否存在）
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new ServiceException("商品不存在");
        }
        
        // 检查商品是否上架
        if (product.getStatus() != 1) {
            throw new ServiceException("商品已下架");
        }
        
        // 查询购物车中是否已存在该商品
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .eq(CartItem::getProductId, productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);
        
        if (cartItem != null) {
            // 如果已存在，更新数量
            int newQuantity = cartItem.getQuantity() + quantity;
            
            // 检查库存是否充足
            if (product.getStock() < newQuantity) {
                throw new ServiceException("商品库存不足");
            }
            
            cartItem.setQuantity(newQuantity);
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(cartItem);
        } else {
            // 检查库存是否充足
            if (product.getStock() < quantity) {
                throw new ServiceException("商品库存不足");
            }
            
            // 如果不存在，创建新的购物车项
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(cartItem);
        }
        
        // 返回购物车DTO
        return getCart(userId);
    }
    
    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 新数量
     * @return 购物车DTO
     */
    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            // 如果数量小于等于0，则移除该商品
            return removeFromCart(userId, productId);
        }
        
        // 获取商品信息（验证商品是否存在）
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new ServiceException("商品不存在");
        }
        
        // 检查库存是否充足
        if (product.getStock() < quantity) {
            throw new ServiceException("商品库存不足");
        }
        
        // 查询购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .eq(CartItem::getProductId, productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);
        
        if (cartItem == null) {
            throw new ServiceException("购物车中不存在该商品");
        }
        
        // 更新商品数量
        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemMapper.updateById(cartItem);
        
        // 返回购物车DTO
        return getCart(userId);
    }
    
    /**
     * 从购物车移除商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车DTO
     */
    @Transactional
    public CartDTO removeFromCart(Long userId, Long productId) {
        // 删除购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .eq(CartItem::getProductId, productId);
        cartItemMapper.delete(queryWrapper);
        
        // 返回购物车DTO
        return getCart(userId);
    }
    
    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 空购物车DTO
     */
    @Transactional
    public CartDTO clearCart(Long userId) {
        // 删除该用户的所有购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId);
        cartItemMapper.delete(queryWrapper);
        
        // 返回空购物车
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(new ArrayList<>());
        cartDTO.setTotalQuantity(0);
        cartDTO.setTotalAmount(BigDecimal.ZERO);
        return cartDTO;
    }
    
    /**
     * 获取购物车
     * @param userId 用户ID
     * @return 购物车DTO
     */
    public CartDTO getCart(Long userId) {
        // 查询用户的所有购物车项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .orderByDesc(CartItem::getUpdateTime);
        List<CartItem> cartItems = cartItemMapper.selectList(queryWrapper);
        
        // 转换为DTO列表（动态获取商品信息）
        List<CartItemDTO> itemDTOList = new ArrayList<>();
        for (CartItem item : cartItems) {
            // 动态获取最新的商品信息
            Product product = productService.getProductById(item.getProductId());
            if (product == null || product.getStatus() != 1) {
                // 如果商品不存在或已下架，跳过该项
                continue;
            }
            
            // 构建DTO
            CartItemDTO dto = new CartItemDTO();
            dto.setProductId(item.getProductId());
            dto.setProductName(product.getName());
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                dto.setProductImage(product.getImages().split(",")[0]);
            }
            dto.setPrice(product.getPrice()); // 使用最新价格
            dto.setQuantity(item.getQuantity());
            dto.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            itemDTOList.add(dto);
        }
        
        // 计算总数量和总金额
        int totalQuantity = itemDTOList.stream().mapToInt(CartItemDTO::getQuantity).sum();
        BigDecimal totalAmount = itemDTOList.stream()
                .map(CartItemDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        // 创建购物车DTO
        CartDTO cartDTO = new CartDTO();
        cartDTO.setItems(itemDTOList);
        cartDTO.setTotalQuantity(totalQuantity);
        cartDTO.setTotalAmount(totalAmount);
        
        return cartDTO;
    }
    
    /**
     * 批量移除购物车商品（用于订单结算后清理）
     * @param userId 用户ID
     * @param productIds 商品ID列表
     */
    @Transactional
    public void removeCartItems(Long userId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getUserId, userId)
                   .in(CartItem::getProductId, productIds);
        int deletedCount = cartItemMapper.delete(queryWrapper);
        
        LOGGER.info("用户 {} 购物车已移除 {} 个商品", userId, deletedCount);
    }
}
