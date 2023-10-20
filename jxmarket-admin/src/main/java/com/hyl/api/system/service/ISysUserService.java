package com.hyl.api.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface ISysUserService extends IService<SysUserEntity> {

    /**
     * 通过资源id获取用户id列表
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Long}>
     */
    List<Long> getUserIdListByResourceId(Long resourceId);


/*
    */
/**
     * 加载用户
     *
     * @param username 用户名
     * @return {@link UserDetails}
     *//*

    UserDetails loadUserByUsername(String username);
*/


    /**
     * 得到用户
     *
     * @param username 用户名
     * @return {@link SysUserEntity}
     */
    SysUserEntity getAdminByUsername(String username);


    /**
     * 获得资源列表
     *
     * @param userId 用户id
     * @return {@link List}<{@link SysPermissionEntity}>
     */
    List<SysPermissionEntity> getResourceList(Long userId);


    /**
     * 查出所属该人的所有菜单信息
     * @param userId
     * @return
     */
    List<Long> queryAllMenuId(String userId);

    /**
     * 分页查询 用户信息
     * @return
     */
    PageUtils queryPage(Map<String, Object> params) throws HylException;

    /**
     * 批量删除用户信息
     * @param userIds
     */
    void deleteBatch(Long[] userIds);

    /**
     * 保存用户
     */
    ResultBean<Object> saveUser( Map<String,Object> map) throws HylException;


    /**
     * 更新用户信息
     */
    ResultBean<Object> updateUser(Map<String, Object> map) throws HylException;

    /***
     * 删除用户信息
     * @param map
     * @return
     */
    ResultBean<Object> delete(Map<String, Object> map) throws HylException;


    /**
     * 重置密码
     * @param map
     * @return
     * @throws HylException
     */
    ResultBean<Object> resetPassword(Map<String, Object> map) throws HylException;


    /**
     * 上传头像
     * @param files
     * @return
     */
    ResultBean<Object> uploadAvatar(MultipartFile files);


    /**
     * 用户修改密码
     * @param map
     * @return
     */
    ResultBean<Object> updatePassword(Map<String, Object> map);


    /**
     * 操作用户状态
     * @param map
     * @return
     */
    ResultBean<Object> updateUserStatus(Map<String, Object> map);
}
