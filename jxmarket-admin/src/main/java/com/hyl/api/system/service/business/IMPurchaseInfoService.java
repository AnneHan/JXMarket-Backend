package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MPurchaseInfoEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 采购信息表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
public interface IMPurchaseInfoService extends IService<MPurchaseInfoEntity> {

    /**
     * 分页查询
     * @return
     */
    PageUtils queryPurchasePage(Map<String, Object> params) throws HylException;

    /**
     * 更新信息
     */
    ResultBean<Object> dealPurchase(Map<String, Object> map) throws HylException;

    /***
     * 删除信息
     * @param map
     * @return
     */
    ResultBean<Object> deletePurchase(Map<String, Object> map) throws HylException;

    MPurchaseInfoEntity queryIdByPurchaseId(String id);
}
