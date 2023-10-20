package com.hyl.api.system.mapper;

import com.hyl.api.system.entity.SysConfigEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;

import java.util.List;

/**
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysConfigMapper extends HylBaseMapper<SysConfigEntity> {

    List<SysConfigEntity> findByConfigKey(String configKey);
}
