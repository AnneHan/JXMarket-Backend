package com.hyl.api.system.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.api.system.entity.business.MGoodQuantityEntity;
import com.hyl.common.api.ResultBean;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;

import java.util.Map;

/**
 * <p>
 * 商品库存表 服务类
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
public interface IMGoodQuantityService extends IService<MGoodQuantityEntity> {

    /**
     * 分页查询
     * @return
     */
    PageUtils queryGoodNumPage(Map<String, Object> params) throws HylException;

    /**
     * 更新信息
     */
    ResultBean<Object> dealGoodNum(Map<String, Object> map) throws HylException;
}
