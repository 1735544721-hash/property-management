package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 房屋实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("house")
public class House extends BaseEntity {
    
    /**
     * 楼栋ID
     */
    private Long buildingId;
    
    /**
     * 单元号
     */
    private Integer unitNumber;
    
    /**
     * 楼层号
     */
    private Integer floorNumber;
    
    /**
     * 房间号
     */
    private String roomNumber;
    
    /**
     * 面积(㎡)
     */
    private BigDecimal area;
    
    /**
     * 业主ID
     */
    private Long ownerId;
    
    /**
     * 状态（0空置/1已入住）
     */
    private Integer status;
    
    /**
     * 楼栋名称（非数据库字段）
     */
    @TableField(exist = false)
    private String buildingName;
    
    /**
     * 业主姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String ownerName;
}

