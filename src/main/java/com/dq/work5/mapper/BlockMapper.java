package com.dq.work5.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dq.work5.pojo.Block;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Mapper
public interface BlockMapper extends BaseMapper<Block> {
    @Select("select * from block where uid=#{uid} and bid=#{bid}")
    Block exists(Block block);
}
