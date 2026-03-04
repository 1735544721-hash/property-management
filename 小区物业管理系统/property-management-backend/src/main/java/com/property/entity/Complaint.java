package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 投诉建议实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("complaint")
public class Complaint extends BaseEntity {
    
    /**
     * 投诉人ID
     */
    private Long userId;
    
    /**
     * 类型（投诉/建议）
     */
    private String type;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态（0待处理/1已处理）
     */
    private Integer status;
    
    /**
     * 回复内容
     */
    private String reply;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 投诉人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;
    
    /**
     * 处理人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String handlerName;
}

