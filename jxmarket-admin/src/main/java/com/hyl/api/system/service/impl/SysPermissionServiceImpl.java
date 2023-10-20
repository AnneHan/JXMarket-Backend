package com.hyl.api.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.api.system.entity.SysRolePermissionEntity;
import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.api.system.mapper.SysPermissionMapper;
import com.hyl.api.system.mapper.SysRolePermissionMapper;
import com.hyl.api.system.service.ISysPermissionService;
import com.hyl.api.system.service.ISysRolePermissionService;
import com.hyl.api.system.service.ISysUserService;
import com.hyl.api.system.service.SysLoginService;
import com.hyl.common.api.ResultBean;
import com.hyl.common.constants.HttpConstant;
import com.hyl.common.enums.ResponseCodeEnum;
import com.hyl.common.exception.HylException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermissionEntity> implements ISysPermissionService {

    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ISysRolePermissionService rolePermissionService;
    @Resource
    private SysRolePermissionMapper rolePermissionMapper;
    @Resource
    private SysLoginService loginService;
    @Resource
    ISysUserService userService;

    private final static String plat = "00";

    /**
     * 导航栏菜单
     *
     * @param userId
     * @return
     */
    @Override
    public List<SysPermissionEntity> getUserMenuList(String userId) {
        //系统管理员，拥有最高权限
        /*if (userId.equalsIgnoreCase(GlobalConstant.SUPER_ADMIN)) {
            return getMenuList(null);
        }*/
        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        //如果用户角色为空则直接返回
        if (CollectionUtils.isEmpty(menuIdList)) {
            return null;
        }
        return getMenuList(menuIdList);
    }

    /**
     * 获取拥有的菜单列表
     *
     * @param menuIdList
     * @return
     */
    @Override
    public List<SysPermissionEntity> getMenuList(List<Long> menuIdList) {
        // 查询拥有的所有菜单
        List<SysPermissionEntity> menus = this.baseMapper.selectList(new QueryWrapper<SysPermissionEntity>()
                .in(!CollectionUtils.isEmpty(menuIdList), "id", menuIdList).in("status", 0, 1).orderByDesc("sort_no"));
        // 将id和菜单绑定
        HashMap<String, SysPermissionEntity> menuMap = new HashMap<>(12);
        for (SysPermissionEntity s : menus) {
            menuMap.put(s.getId(), s);
        }
        // 使用迭代器,组装菜单的层级关系
        Iterator<SysPermissionEntity> iterator = menus.iterator();
        while (iterator.hasNext()) {
            SysPermissionEntity menu = iterator.next();
            SysPermissionEntity parent = menuMap.get(menu.getParentId());
            if (Objects.nonNull(parent)) {
                parent.getChild().add(menu);
                // 将这个菜单从当前节点移除
                iterator.remove();
            }
        }
        return menus;
    }

    /**
     * 查询出所有菜单信息
     *
     * @return
     */
    @Override
    public List<SysPermissionEntity> getAllUserMenuList() {
        return getMenuList(null);
    }

    /**
     * 查询是否有子菜单
     *
     * @param menuId
     * @return
     */
    @Override
    public List<SysPermissionEntity> queryListParentId(long menuId) {
        return this.baseMapper.queryListParentId(menuId);
    }


    /**
     * 删除菜单
     *
     * @param menuId
     */
    @Override
    public void delete(long menuId) {
        //删除菜单
        this.removeById(menuId);
        //删除菜单与角色关联
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("permission_id", menuId);
        rolePermissionService.removeByMap(map);
    }

    /**
     * 校验数据
     *
     * @param menu
     * @return
     */
    @Override
    public StringBuffer verifyForm(SysPermissionEntity menu) {
        StringBuffer buffer = new StringBuffer();
        if (StringUtils.isBlank(menu.getName())) {
            buffer.append("菜单名称不能为空;");
        }

        //这是父菜单
        if (null != menu.getMenuType()) {
            if (0 == menu.getMenuType()) {
                //子菜单需要制定父菜单
                if (!StringUtils.isBlank(menu.getParentId())) {
                    buffer.append("当前新增为一级菜单,父菜单ID 应为空值;");
                }

            } else {
                if (StringUtils.isBlank(menu.getParentId())) {
                    buffer.append("二级菜单需要指定一级菜单;");
                }
                //子菜单
                if (StringUtils.isBlank(menu.getUrl())) {
                    buffer.append("当前新增菜单不为一级菜单,菜单URL不能为空;");
                }
            }
        }

        //如果菜单的父id为空，则url不能为空

        return buffer;
    }


    /**
     * 根据角色信息更新菜单信息
     *
     * @param map
     * @return
     */
    @Override
    @Transactional
    public ResultBean updateByRoleId(Map<String, Object> map) throws HylException {
        String roleId = (String) map.get("roleId");
        List<Object> persissionArrays = Arrays.asList(map.get("persissionArrays"));
        List<String> list = (List<String>) persissionArrays.get(0);
        if (StringUtils.isEmpty(roleId) || CollectionUtils.isEmpty(persissionArrays)) {
            return ResultBean.error("数据异常,缺少必填参数", "ERROR", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            //1.先删除相关的角色相关的 菜单信息
            rolePermissionMapper.deleteBatchByMap(roleId);
            //2.添加相关的菜单信息
            List<SysRolePermissionEntity> rolePermissionEntityArrayList = new ArrayList<>();
            SysUserEntity loginUser = loginService.getLoginUser();
            if (!CollectionUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    String perId = list.get(i);
                    SysRolePermissionEntity rolePermissionEntity = new SysRolePermissionEntity();
                    rolePermissionEntity.setPermissionId(String.valueOf(perId));
                    rolePermissionEntity.setRoleId(roleId);
                    rolePermissionEntity.setUpdateBy(loginUser.getUsername());
                    rolePermissionEntityArrayList.add(rolePermissionEntity);
                }
            }
            rolePermissionMapper.insertAllBatch(rolePermissionEntityArrayList);
        } catch (Exception e) {
            log.error("修改角色菜单信息异常;"+e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 批量删除菜单
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBean deleteBatch(Map<String, Object> map) {
        if (null == map || map.isEmpty()) {
            return ResultBean.error("数据异常,缺少必填参数", "REQUIRED EMPTY", ResponseCodeEnum.SYSTEM_ERROR, HttpConstant.DEFAULT_LANGUAGE);
        }
        try {
            //删除相关的主键ID
            List<Object> persissionArrays = Arrays.asList(map.get("ids"));
            List<Long> list = (List<Long>) persissionArrays.get(0);
            baseMapper.deleteBatchIds(list);
            if (!CollectionUtils.isEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    long longId = Long.parseLong(String.valueOf(list.get(i)));
                    List<SysPermissionEntity> sysPermissionEntities = baseMapper.queryListParentId(longId);
                    if (!CollectionUtils.isEmpty(sysPermissionEntities)) {
                        sysPermissionEntities.forEach(sysPermissionEntity -> {
                            baseMapper.deleteById(sysPermissionEntity.getId());
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("更新菜单异常"+e.getMessage());
            throw e;
        }
        return ResultBean.ok(ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 查询首页菜单相关
     *
     * @param userId
     * @return
     */
    @Override
    public ResultBean nav(String userId) {
        SysUserEntity userEntity = userService.getBaseMapper().selectById(userId);
        List<SysPermissionEntity> menuList = getUserMenuList(userId);
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("menuList", menuList);
        map.put("user", userEntity);
        //todo
        map.put("lastLoginData", userEntity.getCreateTime());
        return ResultBean.ok(map, ResponseCodeEnum.HTTP_SUCCESS_CODE_200, HttpConstant.DEFAULT_LANGUAGE);
    }


    /**
     * 查询菜单 --根据类型查询
     *
     * @param map
     * @return
     */
    @Override
    public List<SysPermissionEntity> queryListByType(Map<String, Object> map) {
        String type = (String) map.get("type");
        return this.baseMapper.selectList(new QueryWrapper<SysPermissionEntity>()
                .eq("menu_type", type));
    }


    /**
     * 根据关键字查询菜案信息
     *
     * @param map 关键字
     * @return
     */
    @Override
    public List<SysPermissionEntity> keyWord(Map<String, Object> map) {

        Object keyWord = map.get("keyWord");
        if (null == keyWord || StringUtils.isEmpty(keyWord.toString().trim())) {
            //关键字为空查询所有信息
            return getAllUserMenuList();
        }
        return this.baseMapper.selectList(new QueryWrapper<SysPermissionEntity>()
                .like("name", keyWord.toString()));
    }

}
