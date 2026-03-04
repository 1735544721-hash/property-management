package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Fee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 费用Mapper
 */
@Mapper
public interface FeeMapper extends BaseMapper<Fee> {
    
    /**
     * 分页查询费用信息（含房屋和业主信息）
     */
    @Select("<script>" +
            "SELECT f.*, " +
            "CONCAT(b.building_name, '-', h.unit_number, '单元-', h.room_number) as house_info, " +
            "u.real_name as owner_name " +
            "FROM fee f " +
            "LEFT JOIN house h ON f.house_id = h.id " +
            "LEFT JOIN building b ON h.building_id = b.id " +
            "LEFT JOIN sys_user u ON h.owner_id = u.id " +
            "WHERE f.deleted = 0 " +
            "<if test='houseId != null'>AND f.house_id = #{houseId}</if> " +
            "<if test='feeType != null and feeType != \"\"'>AND f.fee_type = #{feeType}</if> " +
            "<if test='status != null'>AND f.status = #{status}</if> " +
            "<if test='feeMonth != null and feeMonth != \"\"'>AND f.fee_month = #{feeMonth}</if> " +
            "ORDER BY f.id DESC" +
            "</script>")
    IPage<Fee> selectFeePage(Page<Fee> page, @Param("houseId") Long houseId, 
                              @Param("feeType") String feeType, @Param("status") Integer status, 
                              @Param("feeMonth") String feeMonth);
}

