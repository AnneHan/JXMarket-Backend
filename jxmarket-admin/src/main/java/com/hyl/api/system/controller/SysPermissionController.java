package com.hyl.api.system.controller;

import com.hyl.api.system.annotation.SysLog;
import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.service.ISysPermissionService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 菜单权限
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@RestController
@RequestMapping("/system/sys-permission-entity")
public class SysPermissionController {

    @Resource
    ISysPermissionService permissionService;

    @Resource
    private SysLoginService loginService;


    /**
     * 导航菜单
     */
    @SysLog(value = "操作日志--查询菜单", logType = 0)
    @GetMapping("/nav")
    public ResultBean<Object> queryNav(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        return permissionService.nav(userId);
    }

    /**
     * 删除菜单信息 并删除掉与之对应的角色权限信息；
     *
     * @param
     * @return
     */
    @SysLog(value = "操作日志--删除菜单信息", logType = 0)
    @GetMapping("/delete")
    public ResultBean<Object> delete(@RequestParam(required = true) Long menuId) {
        //判断是否有子菜单或按钮
        List<SysPermissionEntity> menuList = permissionService.queryListParentId(menuId);
        if (menuList.size() > 0) {
            return ResultBean.error("请先删除下面所属的子菜单", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        permissionService.delete(menuId);
        return ResultBean.ok("删除成功", ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * @param menu
     * @return
     */
    @SysLog(value = "操作日志--保存新增菜单", logType = 0)
    @PostMapping("/save")
    public ResultBean save(@RequestBody SysPermissionEntity menu) throws HylException {
        //数据校验
        StringBuffer msg = permissionService.verifyForm(menu);
        if (!StringUtils.isEmpty(msg)) {
            return ResultBean.error(msg.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        menu.setCreateBy(loginService.getLoginUser().getUsername());
        permissionService.save(menu);
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    @RequestMapping("/list")
    @SysLog(value = "操作日志--查询所有菜单列表", logType = 0)
    public ResultBean<Object> queryList() {
        List<SysPermissionEntity> menuList = permissionService.getAllUserMenuList();
        return ResultBean.ok(menuList, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 修改
     */
    @SysLog(value = "操作日志--更新菜单", logType = 0)
    @PostMapping("/update")
    public ResultBean update(@RequestBody SysPermissionEntity menu) throws HylException {
        //数据校验
        StringBuffer msg = permissionService.verifyForm(menu);
        if (!StringUtils.isEmpty(msg)) {
            return ResultBean.error(msg.toString(), "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        menu.setUpdateBy(loginService.getLoginUser().getUsername());
        permissionService.updateById(menu);
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 菜单信息
     */
    @GetMapping("/info")
    @SysLog(value = "操作日志--菜单信息", logType = 0)
    public ResultBean<Object> queryInfo(@RequestParam(required = true) Long menuId) {
        return ResultBean.ok(permissionService.getById(menuId), ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 根据角色信息更新菜单信息
     */
    @SysLog(value = "操作日志--根据角色信息更新菜单信息", logType = 0)
    @PostMapping("/updateByRoleId")
    public ResultBean updateByRoleId(@RequestBody Map<String, Object> map) {
        //数据校验
        try {
            ResultBean objectResultBean = permissionService.updateByRoleId(map);
            return objectResultBean;
        }catch (Exception e){
            return ResultBean.error("修改角色菜单信息异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }


    /**
     * 删除菜单信息 并删除掉与之对应的角色权限信息；
     *
     * @param
     * @return
     */
    @SysLog(value = "操作日志--删除菜单信息", logType = 0)
    @PostMapping("/deleteBatch")
    public ResultBean deleteBatch(@RequestBody Map<String, Object> map) {
        try {
            ResultBean objectResultBean = permissionService.deleteBatch(map);
            return  objectResultBean;
        }catch (Exception e){
            return ResultBean.error("批量删除菜单信息异常", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
    }


    /**
     * 查询菜单 --根据类型查询
     *
     * @return
     */
    @PostMapping("/queryListByType")
    @SysLog(value = "操作日志--查询菜单", logType = 0)
    public ResultBean<Object> queryByType(@RequestBody Map<String, Object> map) {
        List<SysPermissionEntity> menuList = permissionService.queryListByType(map);
        return ResultBean.ok(menuList, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 查询菜单 --根据关键字查询
     *
     * @return
     */
    @PostMapping("/keyWord")
    @SysLog(value = "操作日志--关键字查询菜单", logType = 0)
    public ResultBean<Object> queryKeyWord(@RequestBody Map<String, Object> map) {
        List<SysPermissionEntity> menuList = permissionService.keyWord(map);
        return ResultBean.ok(menuList, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

}
