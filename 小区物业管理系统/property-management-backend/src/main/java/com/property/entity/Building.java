package com.property.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 楼栋实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("building")
public class Building extends BaseEntity {
    
    /**
     * 楼栋名称
     */
    private String buildingName;
    
    /**
     * 楼层数
     */
    private Integer floors;
    
    /**
     * 单元数
     */
    private Integer units;
    
    /**
     * 描述
     */
    private String description;
}

