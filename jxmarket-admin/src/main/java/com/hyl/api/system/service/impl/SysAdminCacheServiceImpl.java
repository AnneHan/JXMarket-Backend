package com.hyl.api.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.entity.SysUserRoleEntity;
import com.hyl.api.system.service.ISysUserRoleService;
import com.hyl.api.system.service.ISysUserService;
import com.hyl.api.system.service.SysAdminCacheService;
import com.hyl.common.constants.AdminCacheConstant;
import com.hyl.common.redis.service.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统管理员impl缓存服务
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysAdminCacheServiceImpl implements SysAdminCacheService {

    /**
     * 到期时间 24h
     */
    private static final Long EXPIRE_TIME = 86400L;

    @Resource
    private ISysUserService userService;

    @Resource
    private  RedisService redisService;

    @Resource
    private ISysUserRoleService userRoleService;


    @Override
    public void delUser(Long userId) {
        SysUserEntity sysUserEntity = userService.getById(userId);
        if (sysUserEntity != null) {
            String key = AdminCacheConstant.SYSTEM_USER_ENTITY_CACHE_KEY + sysUserEntity.getUsername() + "-" + userId;
            redisService.del(key);
        }
    }

    @Override
    public void delResourceList(Long userId) {
        String key = AdminCacheConstant.SYSTEM_MENU_LIST_CACHE_KEY + userId;
        redisService.del(key);
    }

    @Override
    public void delResourceListByRole(Long roleId) {
        QueryWrapper<SysUserRoleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserRoleEntity::getRoleId, roleId);
        List<SysUserRoleEntity> userRoleList = userRoleService.list(queryWrapper);
        delResourceByRoleHandle(userRoleList);
    }

    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {
        QueryWrapper<SysUserRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(SysUserRoleEntity::getRoleId, roleIds);
        List<SysUserRoleEntity> userRoleList = userRoleService.list(wrapper);
        delResourceByRoleHandle(userRoleList);
    }

    /**
     * 根据角色删除处理资源
     *
     * @param userRoleEntities 用户角色实体
     */
    private void delResourceByRoleHandle(List<SysUserRoleEntity> userRoleEntities) {
        if (CollUtil.isNotEmpty(userRoleEntities)) {
            List<String> keys = userRoleEntities.stream().map(relation -> AdminCacheConstant.SYSTEM_MENU_LIST_CACHE_KEY + relation.getUserId()).collect(Collectors.toList());
            redisService.del(keys);
        }
    }


    @Override
    public void delResourceListByResource(Long resourceId) {
        List<Long> userIdList = userService.getUserIdListByResourceId(resourceId);
        if (CollUtil.isNotEmpty(userIdList)) {
            List<String> keys = userIdList.stream().map(userId -> AdminCacheConstant.SYSTEM_MENU_LIST_CACHE_KEY + userId).collect(Collectors.toList());
            redisService.del(keys);
        }
    }

    @Override
    public SysUserEntity getUser(String username) {
        String key = AdminCacheConstant.SYSTEM_USER_ENTITY_CACHE_KEY + username;
        return (SysUserEntity) redisService.get(key);
    }

    @Override
    public void setUser(SysUserEntity user) {
        String key = AdminCacheConstant.SYSTEM_USER_ENTITY_CACHE_KEY + user.getUsername();
        redisService.set(key, user, EXPIRE_TIME);
    }

    @Override
    public List<SysPermissionEntity> getResourceList(Long userId) {
        String key = AdminCacheConstant.SYSTEM_USER_ENTITY_CACHE_KEY + userId;
        Object cacheResult = redisService.get(key);
        if (ObjectUtil.isNotEmpty(cacheResult)) {
            return JSONUtil.toList(cacheResult.toString(), SysPermissionEntity.class);
        }
        return null;
    }

    @Override
    public void setResourceList(Long userId, List<SysPermissionEntity> resourceList) {
        String key = AdminCacheConstant.SYSTEM_MENU_LIST_CACHE_KEY + userId;
        redisService.set(key, resourceList, EXPIRE_TIME);
    }
}
