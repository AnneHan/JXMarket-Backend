package com.hyl.api.system.controller.business;


import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.service.business.IMGoodQuantityService;
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
 * 库存管理
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@RestController
@RequestMapping("/business/inventory")
public class MInventoryManageController {

    @Resource
    private IMGoodQuantityService goodQuantityService;

    /**
     * 商品存量列表
     */
    @SysLog(value = "操作日志--查询商品存量信息", logType = 0)
    @PostMapping("/good-num/list")
    public ResultBean<Object> queryGoodNumList(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = goodQuantityService.queryGoodNumPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 商品存量更新
     */
    @SysLog(value = "操作日志--更新商品存量信息", logType = 0)
    @PostMapping("/good-num/deal")
    public ResultBean dealGoodNum(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = goodQuantityService.dealGoodNum(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新商品存量异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }
}
