package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
public class Notice extends BaseEntity {
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告内容
     */
    private String content;
    
    /**
     * 发布人ID
     */
    private Long publisherId;
    
    /**
     * 状态（0草稿/1已发布）
     */
    private Integer status;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 发布人姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String publisherName;
}

