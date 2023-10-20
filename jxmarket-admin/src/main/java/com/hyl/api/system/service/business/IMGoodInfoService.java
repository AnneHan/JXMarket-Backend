package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MGoodInfoEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 商品信息表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
public interface IMGoodInfoService extends IService<MGoodInfoEntity> {

    /**
     * 分页查询
     * @return
     */
    PageUtils queryGoodPage(Map<String, Object> params) throws HylException;

    /**
     * 更新信息
     */
    ResultBean<Object> dealGood(Map<String, Object> map) throws HylException;

    /***
     * 删除信息
     * @param map
     * @return
     */
    ResultBean<Object> deleteGood(Map<String, Object> map) throws HylException;

    MGoodInfoEntity queryIdByGoodId(String id);
}
