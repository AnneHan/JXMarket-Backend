package com.hyl.api.system.mapper;

import com.hyl.api.system.entity.SysPermissionEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysPermissionMapper extends HylBaseMapper<SysPermissionEntity> {

    List<SysPermissionEntity> queryListParentId(long menuId);
}
