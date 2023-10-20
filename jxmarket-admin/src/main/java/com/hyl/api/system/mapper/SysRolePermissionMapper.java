package com.hyl.api.system.mapper;

import com.hyl.api.system.entity.SysRolePermissionEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysRolePermissionMapper extends HylBaseMapper<SysRolePermissionEntity> {


    void deleteBatchByMap(@Param("roleId")String roleId);
}
