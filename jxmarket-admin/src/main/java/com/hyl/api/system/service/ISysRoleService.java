package com.hyl.api.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.SysRoleEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface ISysRoleService extends IService<SysRoleEntity> {


    /**
     * 查询角色列表
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params) throws HylException;

    /**
     * 删除角色
     * @param roleIds
     */
    void deleteBatch(Long[] roleIds);


    /**
     * 删除角色信息
     * @param roleId
     * @return
     */
    boolean deleteById(String roleId);


    /**
     * 保存角色信息
     * @param role
     */
    ResultBean saveRole(SysRoleEntity role) throws HylException;

    /**
     * 通过角色查询菜单信息
     * @param role
     * @return
     */
    ResultBean<Object> permissByRoleId(SysRoleEntity role);
}
