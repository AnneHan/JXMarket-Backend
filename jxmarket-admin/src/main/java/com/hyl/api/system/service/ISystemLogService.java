package com.hyl.api.system.service;

import com.hyl.api.system.entity.SysLogEntity;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;


public interface ISystemLogService {
    void saveData(SysLogEntity sysLog);

    PageUtils queryPage(Map<String, Object> params) throws HylException;
}
