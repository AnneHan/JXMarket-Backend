package com.hyl.api.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.SysConfigEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface ISysConfigService extends IService<SysConfigEntity> {

    SysConfigEntity findByConfigKey(String configKey);
}
