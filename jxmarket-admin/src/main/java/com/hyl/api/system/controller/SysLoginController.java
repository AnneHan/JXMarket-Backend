package com.hyl.api.system.controller;

import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.dto.login.LoginParamDto;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.api.system.vo.login.LoginResultVo;
import com.hyl.common.api.ResultBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录接口
 * @author AnneHan
 * @date 2023-09-15
 */
@RestController
public class SysLoginController {

    @Resource
    SysLoginService loginService;

    /**
     * 运维后台管理系统登录接口
     * @param jsonBody
     * @return
     */
    @PostMapping(value = "system/login")
    @SysLog(value = "登录日志",logType = 1)
    public ResultBean<LoginResultVo> login(@RequestBody LoginParamDto jsonBody) {
        return loginService.login(jsonBody);
    }
}
