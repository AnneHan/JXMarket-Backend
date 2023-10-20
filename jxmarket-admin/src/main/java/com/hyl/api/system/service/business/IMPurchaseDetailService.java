package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MPurchaseDetailEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 采购明细表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
public interface IMPurchaseDetailService extends IService<MPurchaseDetailEntity> {

    /**
     * 分页查询
     * @return
     */
    PageUtils queryPurchaseDetPage(Map<String, Object> params) throws HylException;

    /**
     * 更新信息
     */
    ResultBean<Object> dealPurchaseDet(Map<String, Object> map) throws HylException;

    /***
     * 删除信息
     * @param map
     * @return
     */
    ResultBean<Object> deletePurchaseDet(Map<String, Object> map) throws HylException;

    MPurchaseDetailEntity queryIdByPurchaseId(String id);
    MPurchaseDetailEntity queryIdByPurchaseDetId(String id);
}
