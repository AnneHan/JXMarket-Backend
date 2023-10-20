package com.hyl.api.system.mapper;

import com.hyl.api.system.entity.SysUserEntity;
import com.hyl.core.mybatis.mapper.HylBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统用户表 Mapper 接口
 * </p>
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public interface SysUserMapper extends HylBaseMapper<SysUserEntity> {

    /**
     * 通过资源id获取用户id列表
     *
     * @param resourceId 资源id
     * @return {@link List}<{@link Long}>
     */
    List<Long> getUserIdListByResourceId(@Param("resourceId") Long resourceId);


    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);

    /**
     * 查询用户信息
     * @param username
     * @return
     */
    int selectByName(String username);
}
