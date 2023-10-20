package com.hyl.api.system.controller.business;


import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.service.business.IMGoodInfoService;
import com.hyl.api.system.service.business.IMSupplierInfoService;
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
 * 采购管理
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@RestController
@RequestMapping("/business/purchase")
public class MPurchaseManageController {

    @Resource
    private IMSupplierInfoService supplierInfoService;
    @Resource
    private IMGoodInfoService goodInfoService;

    /**
     * 供应商列表
     */
    @SysLog(value = "操作日志--查询供应商信息", logType = 0)
    @PostMapping("/supplier/list")
    public ResultBean<Object> querySupplierList(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = supplierInfoService.querySupplierPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 供应商更新
     */
    @SysLog(value = "操作日志--更新供应商信息", logType = 0)
    @PostMapping("/supplier/deal")
    public ResultBean dealSupplier(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = supplierInfoService.dealSupplier(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新供应商异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 供应商删除
     */
    @SysLog(value = "操作日志--删除供应商信息", logType = 0)
    @PostMapping("/supplier/delete")
    public ResultBean deleteSupplier(@RequestBody Map<String, Object> map) throws HylException {
        try {
            ResultBean delete = supplierInfoService.deleteSupplier(map);
            return delete;
        }catch (Exception e){
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 商品列表
     */
    @SysLog(value = "操作日志--查询商品信息", logType = 0)
    @PostMapping("/good/list")
    public ResultBean<Object> queryGoodList(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = goodInfoService.queryGoodPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 商品更新
     */
    @SysLog(value = "操作日志--更新商品信息", logType = 0)
    @PostMapping("/good/deal")
    public ResultBean dealGood(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = goodInfoService.dealGood(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新商品异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 商品删除
     */
    @SysLog(value = "操作日志--删除商品信息", logType = 0)
    @PostMapping("/good/delete")
    public ResultBean deleteGood(@RequestBody Map<String, Object> map) throws HylException {
        try {
            ResultBean delete = goodInfoService.deleteGood(map);
            return delete;
        }catch (Exception e){
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }
}
