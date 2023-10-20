package com.hyl.api.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.SysUserRoleEntity;

import java.util.List;


/**
 * sys用户角色服务
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface ISysUserRoleService extends IService<SysUserRoleEntity> {

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    List<Long> queryRoleIdList(Long id);

    SysUserRoleEntity queryIdByUserId(String id);
}
