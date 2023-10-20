package com.hyl.api.system.service;


import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.entity.SysUserEntity;

import java.util.List;

/**
 * 系统管理员缓存服务
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysAdminCacheService {

    /**
     * 删除用户
     *
     * @param userId 用户id
     */
    void delUser(Long userId);

    /**
     * 删除后台用户资源列表缓存
     *
     * @param userId 用户id
     */
    void delResourceList(Long userId);

    /**
     * 当角色相关资源信息改变时删除相关后台用户缓存
     *
     * @param roleId 角色id
     */
    void delResourceListByRole(Long roleId);

    /**
     * 当角色相关资源信息改变时删除相关后台用户缓存
     *
     * @param roleIds 角色id
     */
    void delResourceListByRoleIds(List<Long> roleIds);

    /**
     * 当资源信息改变时，删除资源项目后台用户缓存
     *
     * @param resourceId 资源id
     */
    void delResourceListByResource(Long resourceId);

    /**
     * 获取缓存后台用户信息
     *
     * @param username 用户名
     * @return {@link SysUserEntity}
     */
    SysUserEntity getUser(String username);

    /**
     * 设置缓存后台用户信息
     *
     * @param user 用户
     */
    void setUser(SysUserEntity user);

    /**
     * 获取缓存后台用户资源列表
     *
     * @param userId 用户id
     * @return {@link List}<{@link SysPermissionEntity}>
     */
    List<SysPermissionEntity> getResourceList(Long userId);

    /**
     * 设置后台后台用户资源列表
     *
     * @param userId       用户id
     * @param resourceList 资源列表
     */
    void setResourceList(Long userId, List<SysPermissionEntity> resourceList);
}
