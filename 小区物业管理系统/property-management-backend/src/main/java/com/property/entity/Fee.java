package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 费用实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fee")
public class Fee extends BaseEntity {
    
    /**
     * 房屋ID
     */
    private Long houseId;
    
    /**
     * 费用类型（物业费/水费/电费/停车费）
     */
    private String feeType;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 费用月份（如2024-01）
     */
    private String feeMonth;
    
    /**
     * 状态（0未缴/1已缴）
     */
    private Integer status;
    
    /**
     * 缴费时间
     */
    private LocalDateTime payTime;
    
    /**
     * 房屋信息（非数据库字段）
     */
    @TableField(exist = false)
    private String houseInfo;
    
    /**
     * 业主姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String ownerName;
}

