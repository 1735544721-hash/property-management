package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 房屋Mapper
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {
    
    /**
     * 分页查询房屋信息（含楼栋名称和业主姓名）
     */
    @Select("<script>" +
            "SELECT h.*, b.building_name, u.real_name as owner_name " +
            "FROM house h " +
            "LEFT JOIN building b ON h.building_id = b.id " +
            "LEFT JOIN sys_user u ON h.owner_id = u.id " +
            "WHERE h.deleted = 0 " +
            "<if test='buildingId != null'>AND h.building_id = #{buildingId}</if> " +
            "<if test='status != null'>AND h.status = #{status}</if> " +
            "ORDER BY h.id DESC" +
            "</script>")
    IPage<House> selectHousePage(Page<House> page, @Param("buildingId") Long buildingId, @Param("status") Integer status);
    
    /**
     * 查询业主的房屋列表（含楼栋名称）
     */
    @Select("SELECT h.*, b.building_name " +
            "FROM house h " +
            "LEFT JOIN building b ON h.building_id = b.id " +
            "WHERE h.deleted = 0 AND h.owner_id = #{ownerId} " +
            "ORDER BY h.id DESC")
    java.util.List<House> selectByOwnerId(@Param("ownerId") Long ownerId);
}

