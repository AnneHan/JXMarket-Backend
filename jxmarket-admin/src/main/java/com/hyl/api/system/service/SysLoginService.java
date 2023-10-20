package com.hyl.api.system.service;

import com.hyl.api.system.dto.login.LoginParamDto;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.vo.login.LoginResultVo;
import com.hyl.common.api.ResultBean;
import com.hyl.common.domain.JwtData;
import com.hyl.common.exception.HylException;
import org.springframework.stereotype.Service;

@Service
public interface SysLoginService {

    /**
     * 清除登录缓存数据
     *
     * @param jwtData jwt数据
     */
    void clearLoginCacheData(JwtData jwtData);


    /**
     * 获取登录用户
     *
     * @return {@link SysUserEntity}
     * @throws HylException Exception
     */
    SysUserEntity getLoginUser() throws HylException;

    /**
     * 后台运维管理系统登录接口
     * @param jsonBody
     * @return
     */
    ResultBean<LoginResultVo> login(LoginParamDto jsonBody);

    SysUserEntity getUserInfo(LoginParamDto jsonBody);
}
