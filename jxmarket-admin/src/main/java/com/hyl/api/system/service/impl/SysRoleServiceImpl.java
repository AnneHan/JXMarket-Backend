package com.hyl.api.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hyl.api.system.entity.SysRoleEntity;
import com.hyl.api.system.entity.SysRolePermissionEntity;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.mapper.SysRoleMapper;
import com.hyl.api.system.mapper.SysRolePermissionMapper;
import com.hyl.api.system.service.ISysPermissionService;
import com.hyl.api.system.service.ISysRoleService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import com.hyl.core.common.PageUtils;
import com.hyl.core.common.Query;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleEntity> implements ISysRoleService {

    @Resource
    private SysLoginService loginService;
    @Resource
    private SysRolePermissionMapper rolePermissionMapper;
    @Resource
    private ISysPermissionService permissionService;

    /**
     * 查询角色列表
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) throws HylException {
        String roleName = (String) params.get("roleName");
        IPage<SysRoleEntity> page = this.page(
                new Query<SysRoleEntity>().getPage(params),
                new QueryWrapper<SysRoleEntity>()
                        .like(StringUtils.isNotBlank(roleName), "role_name", roleName)
                        .like(ObjectUtils.isNotEmpty(params.get("roleCode")), "role_code", params.get("roleCode"))
                        .like(ObjectUtils.isNotEmpty(params.get("description")), "description", params.get("description")).
                        orderByDesc("create_time")
        );
        return new PageUtils(page);
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds
     */
    @Override
    public void deleteBatch(Long[] roleIds) {
        //this.removeByIds(Arrays.asList(roleIds));
        try {
            if(null!=roleIds&&roleIds.length>0){
                Arrays.stream(roleIds).forEach((id)->{
                    this.deleteById(String.valueOf(id));
                });
            }
        }catch (Exception e){
            log.error("批量删除角色信息异常");
        }
    }

    /**
     * 删除角色信息
     *
     * @param roleId
     * @return
     */
    @Override
    @Transactional
    public boolean deleteById(String roleId) {
        try {
            this.removeById(roleId);
            Map<String, Object> dataMap  = Maps.newHashMap();
            dataMap.put("role_id",roleId);
            rolePermissionMapper.deleteByMap(dataMap);
            return true;
        } catch (Exception e) {
            log.error("删除角色信息异常");
        }
        return false;
    }


    /**
     * 保存角色信息
     *
     * @param role
     */
    @Override
    public ResultBean saveRole(SysRoleEntity role) throws HylException {
        if (StringUtils.isEmpty(role.getDescription()) || StringUtils.isEmpty(role.getRoleCode()) || StringUtils.isEmpty(role.getRoleName())) {
            log.error("系统配置-增加角色信息必填字段不能为空");
            return ResultBean.error("增加角色信息必填字段不能为空", "REQUIRED EMPTY", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("role_code", role.getRoleCode());
        List<SysRoleEntity> sysRoleEntities = this.baseMapper.selectByMap(map);

        if (!CollectionUtils.isEmpty(sysRoleEntities)) {
            return ResultBean.error("角色编码唯一,请重新填写!", "ERROR CODE ONLY ONE ", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        SysUserEntity accuser = loginService.getLoginUser();
        role.setCreateBy(accuser.getUsername());
        try {
            this.save(role);
        } catch (Exception e) {
            log.error("系统配置-增加角色信息异常" + e.getMessage());
            return ResultBean.error("增加角色信息异常", "ADD ROLE ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }

    /**
     * 通过角色查询菜单信息
     *
     * @param role
     * @return
     */
    @Override
    public ResultBean permissByRoleId(SysRoleEntity role) {
        List<SysRolePermissionEntity> role_id = rolePermissionMapper.
                selectList(new QueryWrapper<SysRolePermissionEntity>
                        ().eq("role_id", role.getId()));
        List<Long> idList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(role_id)) {
            role_id.forEach(id -> {
                idList.add(Long.parseLong(id.getPermissionId()));
            });
        }
        if (!CollectionUtils.isEmpty(idList)) {
            return ResultBean.ok(permissionService.getMenuList(idList), ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
        }
        return ResultBean.ok(null, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }
}
