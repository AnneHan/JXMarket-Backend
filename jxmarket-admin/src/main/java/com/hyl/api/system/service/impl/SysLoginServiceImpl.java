package com.hyl.api.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hyl.api.system.dto.login.LoginParamDto;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.service.ISysUserService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.vo.login.LoginResultVo;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.CacheConstant;
import com.hyl.common.constants.GlobalConstant;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.domain.JwtData;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.common.redis.service.RedisService;
import com.hyl.common.thread.ThreadLocalRealize;
import com.hyl.common.utils.AesEncodeUtil;
import com.hyl.common.utils.HylDateUtils;
import com.hyl.common.utils.JwtGenerateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.hyl.common.enums.ResponseCodeEnum.LOGIN_FAILED;
import static com.hyl.common.enums.ResponseCodeEnum.PASSWORD_IS_EMPTY;

/**
 * 登录接口
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysLoginServiceImpl implements SysLoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysLoginServiceImpl.class);

    @Resource
    private ISysUserService userService;
    @Resource
    private RedisService redisService;

    @Override
    public void clearLoginCacheData(JwtData jwtData) {
        redisService.clearLoginCacheData(jwtData);
    }

    @Override
    public SysUserEntity getLoginUser() throws HylException {
        JwtData jwtData = ThreadLocalRealize.getJwtData();
        if (jwtData == null) {
            throw new HylException(ResponseCodeEnum.TOKEN_IS_EXPIRED, HttpConstant.DEFAULT_LANGUAGE);
        }
        Object data = redisService.get(CacheConstant.OAUTH_USER_INFO_KEY + jwtData.getAccount());
        if (null == data) {
            throw new HylException(ResponseCodeEnum.TOKEN_IS_EXPIRED, HttpConstant.DEFAULT_LANGUAGE);
        }
        SysUserEntity userEntity = JSONObject.parseObject(data.toString(), SysUserEntity.class);
        LoginParamDto loginDto = new LoginParamDto();
        loginDto.setUsername(jwtData.getAccount());
        SysUserEntity user = getUserInfo(loginDto);
        if (null == user) {
            LOGGER.error("用户->:{}未入库，非法用户访问", jwtData.getAccount());
            throw new HylException(ResponseCodeEnum.LOGIN_IS_EXPIRED, HttpConstant.DEFAULT_LANGUAGE);
        }
        return userEntity;
    }

    /**
     * @param jsonBody
     * @return
     */
    @Override
    public ResultBean<LoginResultVo> login(LoginParamDto jsonBody) {
        //获取登录账号和密码与数据库进行比对
        //参数校验
        if (StringUtils.isEmpty(jsonBody.getUsername())) {
            return ResultBean.error(null, ResponseCodeEnum.ACCOUNT_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        if (StringUtils.isEmpty(jsonBody.getPassword())) {
            return ResultBean.error(null, PASSWORD_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("username", jsonBody.getUsername());
        List<SysUserEntity> sysUserEntities = userService.getBaseMapper().selectByMap(map);
        if(CollectionUtils.isEmpty(sysUserEntities)){
            return ResultBean.error(null, LOGIN_FAILED, HttpConstant.DEFAULT_LANGUAGE);
        }
        SysUserEntity userEntity = sysUserEntities.get(0);
        if (null == userEntity || GlobalConstant.USER_STATUS != userEntity.getStatus()) {
            return ResultBean.error(null, ResponseCodeEnum.ACCOUNT_FREEZE, HttpConstant.DEFAULT_LANGUAGE);
        }
        //将前端传过来的密码进行解密
        byte[] decode = Base64.getDecoder().decode(jsonBody.getPassword());
        String password = AesEncodeUtil.decrypt(new String(decode, StandardCharsets.UTF_8));
        //验证密码是否正确
        byte[] decode1 = Base64.getDecoder().decode(userEntity.getPassword());
        String password1 = AesEncodeUtil.decrypt(new String(decode1, StandardCharsets.UTF_8));
        if (StringUtils.isEmpty(password1)||StringUtils.isEmpty(password)||
                !password.equals(password1)) {
            return ResultBean.error(null, LOGIN_FAILED, HttpConstant.DEFAULT_LANGUAGE);
        }
        //登录信息存储system_log
        String token = setUserInfo(jsonBody, userEntity);
        //查出该人员对应的权限以及菜单信息 ；
        LoginResultVo loginResultVo = new LoginResultVo();
        loginResultVo.setToken(token);
        loginResultVo.setId(userEntity.getId());
        return ResultBean.ok(loginResultVo, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 设置登录信息，以及返回token信息；
     *
     * @param jsonBody
     * @param userEntity
     * @return token
     */
    private String setUserInfo(LoginParamDto jsonBody, SysUserEntity userEntity) {
        String account = jsonBody.getUsername();

        JwtData jwtData = new JwtData();
        jwtData.setAccount(account);
        jwtData.setLanguage(HttpConstant.DEFAULT_LANGUAGE);
        Date futureDay = HylDateUtils.getNextForDay(90);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(futureDay);
        calendar.add(Calendar.MINUTE, -5);
        Date overdueDate = calendar.getTime();
        //生成token
        String token = JwtGenerateUtil.createToken(jwtData, futureDay);
        //redis 将toKen和user对象放入redis
        redisService.set(CacheConstant.SYS_LANGUAGE_KEY + account, HttpConstant.DEFAULT_LANGUAGE);
        redisService.set(CacheConstant.OAUTH_USER_TOKEN_KEY + account, token, overdueDate.getTime());
        redisService.set(CacheConstant.OAUTH_USER_INFO_KEY + account, JSONObject.toJSONString(userEntity), overdueDate.getTime());

        redisService.set(CacheConstant.OAUTH_USER_INFO_KEY_SYSTEM_LOGIN_NAME ,jsonBody.getUsername() );

        return token;
    }

    @Override
    public SysUserEntity getUserInfo(LoginParamDto jsonBody){
        HashMap<String,Object> map = new HashMap<>();
        map.put("username", jsonBody.getUsername());
        List<SysUserEntity> sysUserEntities = userService.getBaseMapper().selectByMap(map);
        if(CollectionUtils.isEmpty(sysUserEntities)){
            LOGGER.error("getUserInfo()查无数据>>>[{}]", jsonBody.getUsername());
        }
        SysUserEntity userEntity = sysUserEntities.get(0);
        if (null == userEntity || GlobalConstant.USER_STATUS != userEntity.getStatus()) {
            LOGGER.error("getUserInfo()用户失效>>>[{}]", jsonBody.getUsername());
        }
        return userEntity;
    }
}
