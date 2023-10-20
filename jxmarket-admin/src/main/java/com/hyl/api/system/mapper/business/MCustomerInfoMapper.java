package com.hyl.api.system.mapper.business;

import com.hyl.api.system.entity.business.MCustomerInfoEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;

/**
 * <p>
 * 客户信息表 Mapper 接口
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-18
 */
public interface MCustomerInfoMapper extends HylBaseMapper<MCustomerInfoEntity> {

    /**
     * 查询用户信息
     * @param username
     * @return
     */
    int selectByName(String username);
}
