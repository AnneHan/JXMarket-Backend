package com.hyl.api.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.api.system.entity.SysUserRoleEntity;
import com.hyl.api.system.mapper.SysUserRoleMapper;
import com.hyl.api.system.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户角色服务impl
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity> implements ISysUserRoleService {

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    @Override
    public List<Long> queryRoleIdList(Long id) {
        return this.baseMapper.queryRoleIdList(id);
    }

    /**
     * 通过userId 查询出 相关信息
     * @param id
     * @return
     */
    @Override
    public SysUserRoleEntity queryIdByUserId(String id) {
        return this.baseMapper.selectOne(new QueryWrapper<SysUserRoleEntity>().eq("user_id",id));
    }


}
