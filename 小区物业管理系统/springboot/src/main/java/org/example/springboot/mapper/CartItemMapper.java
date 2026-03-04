package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.CartItem;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}

