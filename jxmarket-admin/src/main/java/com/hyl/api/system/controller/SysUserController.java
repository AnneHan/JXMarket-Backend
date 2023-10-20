package com.hyl.api.system.controller;


import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.service.ISysUserService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统用户
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Resource
    private ISysUserService sysUserService;

    /**
     * 所有用户列表
     */
    @SysLog(value = "操作日志--查询用户信息", logType = 0)
    @PostMapping("/list")
    public ResultBean<Object> queryList(@RequestBody Map<String, Object> params) throws HylException {
        PageUtils page = sysUserService.queryPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 保存用户
     */
    @SysLog(value = "操作日志--保存用户信息", logType = 0)
    @PostMapping("/save")
    public ResultBean save(@RequestBody Map<String, Object> map) throws HylException {
        try {
            ResultBean objectResultBean = sysUserService.saveUser(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("新增用户异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 修改用户
     */
    @SysLog(value = "操作日志--修改用户信息", logType = 0)
    @PostMapping("/update")
    public ResultBean update(@RequestBody Map<String, Object> map) {

        try {
            ResultBean objectResultBean = sysUserService.updateUser(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("更新用户异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }

    /**
     * 删除用户
     */
    @SysLog(value = "操作日志--删除用户信息", logType = 0)
    @PostMapping("/delete")
    public ResultBean delete(@RequestBody Map<String, Object> map) throws HylException {

        try {
            ResultBean delete = sysUserService.delete(map);
            return delete;
        }catch (Exception e){
            return ResultBean.error(ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }


    /**
     * 重置密码
     */
    @SysLog(value = "操作日志--重置密码", logType = 0)
    @PostMapping("/resetPassword")
    public ResultBean<Object> updateResetPassword(@RequestBody Map<String, Object> map) throws HylException {
        return sysUserService.resetPassword(map);
    }


    /**
     * 上传用户头像
     */
    @SysLog(value = "操作日志--上传用户头像", logType = 0)
    @PostMapping("/avatar")
    public ResultBean<Object> uploadAvatar(@RequestPart("files") MultipartFile files
                                          ) throws HylException {
        return sysUserService.uploadAvatar(files);
    }


    /**
     * 修改密码
     */
    @SysLog(value = "操作日志--用户修改密码", logType = 0)
    @PostMapping("/updatePassword")
    public ResultBean<Object> updatePassword(@RequestBody Map<String, Object> map) throws HylException {
        return sysUserService.updatePassword(map);
    }


    /**
     * 修改密码
     */
    @SysLog(value = "操作日志--操作用户状态(冻结或者解除冻结)", logType = 0)
    @PostMapping("/updateUserStatus")
    public ResultBean<Object> updateUserStatus(@RequestBody Map<String, Object> map) throws HylException {
        return sysUserService.updateUserStatus(map);
    }
}
