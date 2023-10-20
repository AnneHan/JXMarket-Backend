package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MCustomerInfoEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 客户信息表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-18
 */
public interface IMCustomerInfoService extends IService<MCustomerInfoEntity> {

    /**
     * 分页查询 用户信息
     * @return
     */
    PageUtils queryPage(Map<String, Object> params) throws HylException;

    /**
     * 保存用户
     */
    ResultBean<Object> saveUser(Map<String,Object> map) throws HylException;

    /**
     * 更新用户信息
     */
    ResultBean<Object> updateUser(Map<String, Object> map) throws HylException;

    /**
     * 更新用户信息
     */
    ResultBean<Object> dealUser(Map<String, Object> map) throws HylException;

    /***
     * 删除用户信息
     * @param map
     * @return
     */
    ResultBean<Object> delete(Map<String, Object> map) throws HylException;

    /**
     * 操作用户状态
     * @param map
     * @return
     */
    ResultBean<Object> updateUserStatus(Map<String, Object> map);
}
