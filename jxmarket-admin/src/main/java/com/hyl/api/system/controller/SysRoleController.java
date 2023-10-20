package com.hyl.api.system.controller;

import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.entity.SysRoleEntity;
import com.hyl.api.system.service.ISysRoleService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 角色
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@RestController
@RequestMapping("/system/sys-role-entity")
public class SysRoleController {

    @Resource
    private ISysRoleService roleService;
    @Resource
    private SysLoginService loginService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @SysLog(value = "操作日志--查询角色列表", logType = 0)
    public ResultBean<Object> queryList(@RequestParam Map<String, Object> params) throws HylException {
        PageUtils page = roleService.queryPage(params);
        return ResultBean.ok(page, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 角色信息
     */
    @PostMapping("/info")
    @SysLog(value = "操作日志--查询角色列表信息", logType = 0)
    public ResultBean queryInfo(@RequestBody Map<String, Object> params) {
        if (null == params.get("id") || "".equalsIgnoreCase(params.get("id").toString())) {
            return ResultBean.error(ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        SysRoleEntity role = roleService.getById(params.get("id").toString());
        return ResultBean.ok(role, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 保存角色
     */
    @PostMapping("/save")
    @SysLog(value = "操作日志--保存角色信息", logType = 0)
    public ResultBean<Object> save(@RequestBody SysRoleEntity role) throws HylException {
        role.setCreateBy(loginService.getLoginUser().getUsername());
        return roleService.saveRole(role);
    }

    /**
     * 修改角色
     */
    @SysLog(value = "操作日志--修改角色信息", logType = 0)
    @PostMapping("/update")
    public ResultBean update(@RequestBody SysRoleEntity role) throws HylException {
        if (StringUtils.isEmpty(role.getId()) || StringUtils.isEmpty(role.getDescription()) || StringUtils.isEmpty(role.getRoleCode()) || StringUtils.isEmpty(role.getRoleName())) {
            return ResultBean.error(ResponseCodeEnum.VALID_IS_EMPTY, HttpConstant.DEFAULT_LANGUAGE);
        }
        role.setUpdateBy(loginService.getLoginUser().getUsername());
        roleService.updateById(role);
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 删除角色
     */
    @SysLog(value = "操作日志--删除角色信息", logType = 0)
    @PostMapping("/delete")
    public ResultBean<Object> delete(@RequestBody SysRoleEntity role) {
        roleService.deleteById(role.getId());
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 通过角色查询菜单信息
     */
    @SysLog(value = "操作日志--通过角色查询菜单信息", logType = 0)
    @PostMapping("/permissByRoleId")
    public ResultBean<Object> queryPermissByRoleId(@RequestBody SysRoleEntity role) {
        return roleService.permissByRoleId(role);
    }


}
