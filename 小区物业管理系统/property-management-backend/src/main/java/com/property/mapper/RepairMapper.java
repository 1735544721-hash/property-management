package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Repair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 报修Mapper
 */
@Mapper
public interface RepairMapper extends BaseMapper<Repair> {
    
    /**
     * 分页查询报修信息（含用户和房屋信息）
     */
    @Select("<script>" +
            "SELECT r.*, u1.real_name as user_name, u2.real_name as handler_name, " +
            "CONCAT(b.building_name, '-', h.unit_number, '单元-', h.room_number) as house_info " +
            "FROM repair r " +
            "LEFT JOIN sys_user u1 ON r.user_id = u1.id " +
            "LEFT JOIN sys_user u2 ON r.handler_id = u2.id " +
            "LEFT JOIN house h ON r.house_id = h.id " +
            "LEFT JOIN building b ON h.building_id = b.id " +
            "WHERE r.deleted = 0 " +
            "<if test='userId != null'>AND r.user_id = #{userId}</if> " +
            "<if test='status != null'>AND r.status = #{status}</if> " +
            "ORDER BY r.id DESC" +
            "</script>")
    IPage<Repair> selectRepairPage(Page<Repair> page, @Param("userId") Long userId, @Param("status") Integer status);
}

