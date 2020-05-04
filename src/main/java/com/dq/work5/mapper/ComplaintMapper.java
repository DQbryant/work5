package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dq.work5.pojo.Complaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {
    @Select("select * from complaint")
    List<Complaint> getAll();
}
