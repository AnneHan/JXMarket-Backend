package com.hyl.api.system.controller.business;


import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.service.business.IMCustomerInfoService;
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
 * <p>
 * 客户信息表 前端控制器
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-18
 */
@RestController
@RequestMapping("/business/sale/custom")
public class MCustomerInfoController {

    @Resource
    private IMCustomerInfoService customerInfoService;

    /**
     * 所有客户列表
     */
    @SysLog(value = "操作日志--查询客户信息", logType = 0)
    @PostMapping("/list")
    public ResultBean<Object> queryList(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = customerInfoService.queryPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 保存客户
     */
    @SysLog(value = "操作日志--保存客户信息", logType = 0)
    @PostMapping("/save")
    public ResultBean save(@RequestBody Map<String, Object> map) throws HylException {
        try {
            ResultBean objectResultBean = customerInfoService.saveUser(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("新增客户异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 修改客户
     */
    @SysLog(value = "操作日志--修改客户信息", logType = 0)
    @PostMapping("/update")
    public ResultBean update(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = customerInfoService.updateUser(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新客户异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 修改客户
     */
    @SysLog(value = "操作日志--更新客户信息", logType = 0)
    @PostMapping("/deal")
    public ResultBean deal(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = customerInfoService.dealUser(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新客户异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 删除客户
     */
    @SysLog(value = "操作日志--删除客户信息", logType = 0)
    @PostMapping("/delete")
    public ResultBean delete(@RequestBody Map<String, Object> map) throws HylException {
        try {
            ResultBean delete = customerInfoService.delete(map);
            return delete;
        }catch (Exception e){
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 操作客户状态
     */
    @SysLog(value = "操作日志--操作客户状态(冻结或者解除冻结)", logType = 0)
    @PostMapping("/updateUserStatus")
    public ResultBean<Object> updateUserStatus(@RequestBody Map<String, Object> map) throws HylException {
        return customerInfoService.updateUserStatus(map);
    }
}
