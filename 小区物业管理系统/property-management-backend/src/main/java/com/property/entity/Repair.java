package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 报修实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("repair")
public class Repair extends BaseEntity {
    
    /**
     * 报修人ID
     */
    private Long userId;
    
    /**
     * 房屋ID
     */
    private Long houseId;
    
    /**
     * 报修标题
     */
    private String title;
    
    /**
     * 报修内容
     */
    private String content;
    
    /**
     * 图片URL（多张用逗号分隔）
     */
    private String images;
    
    /**
     * 状态（0待处理/1处理中/2已完成）
     */
    private Integer status;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理结果
     */
    private String handleResult;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 报修人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;
    
    /**
     * 处理人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String handlerName;
    
    /**
     * 房屋信息（非数据库字段）
     */
    @TableField(exist = false)
    private String houseInfo;
}

