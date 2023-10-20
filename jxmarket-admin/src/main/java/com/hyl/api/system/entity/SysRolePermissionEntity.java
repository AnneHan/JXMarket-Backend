package com.hyl.api.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hyl.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 角色权限表
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_role_permission")
@ApiModel(value = "SysRolePermissionEntity对象", description = "角色权限表")
public class SysRolePermissionEntity extends BaseEntity {

    @ApiModelProperty("角色id")
    @TableField("role_id")
    private String roleId;

    @ApiModelProperty("权限id")
    @TableField("permission_id")
    private String permissionId;


}
