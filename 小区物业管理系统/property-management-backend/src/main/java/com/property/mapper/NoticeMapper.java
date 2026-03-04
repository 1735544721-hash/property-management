package com.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.property.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 公告Mapper
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
    
    /**
     * 分页查询公告信息（含发布人姓名）
     */
    @Select("<script>" +
            "SELECT n.*, u.real_name as publisher_name " +
            "FROM notice n " +
            "LEFT JOIN sys_user u ON n.publisher_id = u.id " +
            "WHERE n.deleted = 0 " +
            "<if test='status != null'>AND n.status = #{status}</if> " +
            "<if test='title != null and title != \"\"'>AND n.title LIKE CONCAT('%', #{title}, '%')</if> " +
            "ORDER BY n.id DESC" +
            "</script>")
    IPage<Notice> selectNoticePage(Page<Notice> page, @Param("status") Integer status, @Param("title") String title);
}

