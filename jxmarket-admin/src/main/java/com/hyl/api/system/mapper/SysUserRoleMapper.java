package com.hyl.api.system.mapper;

import com.hyl.api.system.entity.SysUserRoleEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysUserRoleMapper extends HylBaseMapper<SysUserRoleEntity> {

    List<Long> queryRoleIdList(Long id);
}
