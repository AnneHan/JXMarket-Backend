package com.hyl.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.CacheConstant;
import com.hyl.common.constants.GlobalConstant;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.domain.JwtData;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.common.redis.service.RedisService;
import com.hyl.common.thread.ThreadLocalRealize;
import com.hyl.common.utils.JwtGenerateUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * web mvc拦截器
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public class WebMvcInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebMvcInterceptor.class);

    @Resource
    private RedisService redisService;
    @Resource
    private SysLoginService sysLoginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws HylException, IOException {
        // 验证token有效性
        String token = request.getHeader(GlobalConstant.TOKEN_KEY_NAME);
        // 校验token是否过期
        if (StringUtils.isNotEmpty(token)) {
            boolean verify = JwtGenerateUtil.verify(token);
            if (!verify) {
                logger.error("前端token 格式错误，校验失败！");
                return sendResponse(response, ResultBean.error(ResponseCodeEnum.LOGIN_IS_EXPIRED, HttpConstant.LANGUAGE_EN));
            }
            boolean verifyExpire = JwtGenerateUtil.verifyIsExpired(token, new Date());
            if (!verifyExpire) {
                logger.error("当前时间token已经失效！");
                return sendResponse(response, ResultBean.error(ResponseCodeEnum.LOGIN_IS_EXPIRED, HttpConstant.LANGUAGE_EN));
            }
            String jwtDataJsonStr = JwtGenerateUtil.getJwtParams(GlobalConstant.JWT_DATA_KEY, token);
            JwtData jwtData = JSONObject.parseObject(jwtDataJsonStr, JwtData.class);
            logger.info(">>>>>>>>接口地址:{}, token解密结果:{}<<<<<<<<<", request.getServletPath(), JSONObject.toJSONString(jwtData));
            Object obj = redisService.get(CacheConstant.OAUTH_USER_TOKEN_KEY + jwtData.getAccount());
            if (ObjectUtils.isEmpty(obj)) {
                logger.error("缓存token 不存在，校验失败！");
                sysLoginService.clearLoginCacheData(jwtData);
                return sendResponse(response, ResultBean.error(ResponseCodeEnum.LOGIN_IS_EXPIRED, jwtData.getLanguage()));
            }

            //判断缓存token是否跟数据库token一致
            if (obj.equals(token)) {
                ThreadLocalRealize.setThreadLocalVar(jwtDataJsonStr);
                SysUserEntity user = sysLoginService.getLoginUser();
                if (null == user) {
                    sysLoginService.clearLoginCacheData(jwtData);
                    return false;
                }
                //记录正在操作的用户信息;
                redisService.set(CacheConstant.OAUTH_USER_INFO_KEY_SYSTEM_LOGIN_NAME, user.getUsername());
                return true;
            } else {
                logger.error("前端token 与 缓存token 不一致！");
                return sendResponse(response, ResultBean.error(ResponseCodeEnum.LOGIN_IS_EXPIRED, jwtData.getLanguage()));
            }
        } else {
            logger.error("前端token 为空！");
            return sendResponse(response, ResultBean.error(ResponseCodeEnum.LOGIN_IS_EXPIRED, HttpConstant.LANGUAGE_EN));
        }
    }

    private boolean sendResponse(HttpServletResponse response, ResultBean<Void> resultBean)
            throws IOException {
        OutputStream outputStream = response.getOutputStream();
        // 通过设置响应头控制浏览器以UTF-8的编码显示数据
        response.setHeader("content-type", "application/json;charset=UTF-8");
        String res = JSONObject.toJSONString(resultBean);
        byte[] bytes = res.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        return false;
    }
}
