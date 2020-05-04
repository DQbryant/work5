package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dq.work5.pojo.Block;

/**
 *
 */
public interface BlockService extends IService<Block> {
    boolean exists(Block block);
}
