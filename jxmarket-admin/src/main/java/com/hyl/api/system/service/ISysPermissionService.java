package com.hyl.api.system.service;

import com.hyl.api.system.entity.SysPermissionEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface ISysPermissionService extends IService<SysPermissionEntity> {

    List<SysPermissionEntity> getUserMenuList(String userId);

    /**
     * 查询出所有菜单
     * @return
     */
    List<SysPermissionEntity> getAllUserMenuList();

    /**
     * 查询是否有子菜单
     * @param menuId
     * @return
     */
    List<SysPermissionEntity> queryListParentId(long menuId);

    /**
     * 删除菜单
     * @param menuId
     */
    void delete(long menuId);

    /**
     * 校验数据
     * @param menu
     * @return
     */
    StringBuffer verifyForm(SysPermissionEntity menu);


    /**
     * 根据角色信息更新菜单信息
     * @param map
     * @return
     */
    ResultBean<Object> updateByRoleId(Map<String, Object> map) throws HylException;


    /**
     *
     *
     * 批量删除 菜单信息
     * @return
     */
    ResultBean<Object> deleteBatch(Map<String,Object> map);


    /**
     * 查询首页菜单相关
     * @param userId
     * @return
     */
    ResultBean nav(String userId);

    /**
     * 通过菜单ID 查询菜单
     * @param menuIdList
     * @return
     */
    List<SysPermissionEntity> getMenuList(List<Long> menuIdList) ;


    /**
     * 查询菜单 --根据类型查询
     * @param map
     * @return
     */
    List<SysPermissionEntity> queryListByType(Map<String, Object> map);


    /**
     * 关键字查询菜单
     * @param map 关键字
     * @return
     */
    List<SysPermissionEntity> keyWord(Map<String, Object> map);
}
