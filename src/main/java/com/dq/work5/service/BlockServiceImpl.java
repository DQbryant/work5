package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dq.work5.mapper.BlockMapper;
import com.dq.work5.pojo.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class BlockServiceImpl extends ServiceImpl<BlockMapper, Block> implements BlockService{

    @Override
    public boolean exists(Block block) {
        return baseMapper.exists(block)!=null;
    }
}
