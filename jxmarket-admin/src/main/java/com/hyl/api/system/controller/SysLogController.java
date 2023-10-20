package com.hyl.api.system.controller;

import com.hyl.api.system.service.ISystemLogService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统日志查看
 * @author AnneHan
 * @date 2023-09-15
 */
@RestController
@RequestMapping("/system/sys-log-entity")
public class SysLogController {

    @Resource
    ISystemLogService logService;

    @PostMapping(value = "/list")
    public ResultBean<Object> queryLog(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = logService.queryPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);

    }
}


