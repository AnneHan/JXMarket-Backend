package com.hyl.api.system.service.impl;

import com.hyl.api.system.entity.SysConfigEntity;
import com.hyl.api.system.mapper.SysConfigMapper;
import com.hyl.api.system.service.ISysConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.common.constants.CacheConstant;
import com.hyl.common.redis.service.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigEntity> implements ISysConfigService {

    @Resource
    private RedisService redisService;

    @Override
    public SysConfigEntity findByConfigKey(String configKey) {
        String key = CacheConstant.SYS_CONFIG_CACHE_KEY + configKey;
        SysConfigEntity sysConfigEntity = (SysConfigEntity) redisService.get(key);
        if (null != sysConfigEntity) {
            return sysConfigEntity;
        }
        List<SysConfigEntity> configEntities = this.baseMapper.findByConfigKey(configKey);
        if (CollectionUtils.isNotEmpty(configEntities)) {
            redisService.set(key, configEntities.get(0), 86400L);
            return configEntities.get(0);
        }
        return null;
    }
}
