package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MSupplierInfoEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 供应商信息表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
public interface IMSupplierInfoService extends IService<MSupplierInfoEntity> {

    /**
     * 分页查询
     * @return
     */
    PageUtils querySupplierPage(Map<String, Object> params) throws HylException;

    /**
     * 更新信息
     */
    ResultBean<Object> dealSupplier(Map<String, Object> map) throws HylException;

    /***
     * 删除信息
     * @param map
     * @return
     */
    ResultBean<Object> deleteSupplier(Map<String, Object> map) throws HylException;

    MSupplierInfoEntity queryIdBySupplierId(String id);
}
