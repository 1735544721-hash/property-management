package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 投诉建议Mapper
 */
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
    
    /**
     * 分页查询投诉建议（含用户和处理人姓名）
     */
    @Select("<script>" +
            "SELECT c.*, u1.real_name as user_name, u2.real_name as handler_name " +
            "FROM complaint c " +
            "LEFT JOIN sys_user u1 ON c.user_id = u1.id " +
            "LEFT JOIN sys_user u2 ON c.handler_id = u2.id " +
            "WHERE c.deleted = 0 " +
            "<if test='userId != null'>AND c.user_id = #{userId}</if> " +
            "<if test='type != null and type != \"\"'>AND c.type = #{type}</if> " +
            "<if test='status != null'>AND c.status = #{status}</if> " +
            "ORDER BY c.id DESC" +
            "</script>")
    IPage<Complaint> selectComplaintPage(Page<Complaint> page, @Param("userId") Long userId, 
                                          @Param("type") String type, @Param("status") Integer status);
}

