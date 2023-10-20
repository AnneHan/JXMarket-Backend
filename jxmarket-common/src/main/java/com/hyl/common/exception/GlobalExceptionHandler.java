package com.hyl.common.exception;

import com.hyl.common.constants.HttpConstant;
import com.hyl.common.domain.JwtData;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.api.ResultBean;
import com.hyl.common.redis.service.RedisService;
import com.hyl.common.thread.ThreadLocalRealize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 全局异常处理程序
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Resource
    private RedisService redisService;

    @ResponseBody
    @ExceptionHandler(value = HylException.class)
    public ResultBean<Void> handle(HylException e) {
        LOGGER.error("handle HylException:", e);
        if (e.getResponseCode() != null) {
            if (e.getResponseCode().getCode().equalsIgnoreCase("203")) {
                JwtData jwtData = ThreadLocalRealize.getJwtData();
                if (null != jwtData) {
                    redisService.clearLoginCacheData(jwtData);
                }
            }
            return ResultBean.error(e.getResponseCode(), e.getLanguage());
        }
        return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResultBean<Void> handle(Exception e) {
        LOGGER.error("handle Exception:", e);
        return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
    }
}
