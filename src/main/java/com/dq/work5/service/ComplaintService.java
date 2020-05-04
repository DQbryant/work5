package com.dq.work5.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dq.work5.pojo.Complaint;

import java.util.List;

/**
 *
 */
public interface ComplaintService extends IService<Complaint> {
    List<Complaint> getAll();
}
