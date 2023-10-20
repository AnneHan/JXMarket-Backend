package com.hyl.api.system.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyl.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_permission")
@ApiModel(value = "SysPermissionEntity对象", description = "菜单权限表")
public class SysPermissionEntity extends BaseEntity {


    @ApiModelProperty("父id")
    @TableField("parent_id")
    private String  parentId;

    @ApiModelProperty("菜单标题")

    @TableField(value="name",updateStrategy = FieldStrategy.IGNORED)
    private String name;

    @ApiModelProperty("路径")
    @TableField(value = "url")
    private String url;

    @ApiModelProperty("组件")
    @TableField(value = "component")
    @JsonIgnore
    private String component;

    @ApiModelProperty("组件名字")
    @TableField(value = "component_name")
    @JsonIgnore
    private String componentName;

    @ApiModelProperty("一级菜单跳转地址")
    @TableField(value = "redirect")
    @JsonIgnore
    private String redirect;

    @ApiModelProperty("菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)")
    @TableField("menu_type")
    private Integer menuType;

    @ApiModelProperty("菜单权限编码")
    @TableField("perms")
    @JsonIgnore
    private String perms;

    @ApiModelProperty("权限策略1显示2禁用")
    @TableField("perms_type")
    @JsonIgnore
    private String permsType;

    @ApiModelProperty("菜单排序")
    @TableField("sort_no")
    private Double sortNo;

    @ApiModelProperty("聚合子路由: 1是0否")
    @JsonIgnore
    @TableField("always_show")
    private Boolean alwaysShow;

    @ApiModelProperty("菜单图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty("是否路由菜单: 0:不是  1:是（默认值1）")
    @TableField("is_route")
    @JsonIgnore
    private Boolean isRoute;

    @ApiModelProperty("是否叶子节点:    1:是   0:不是")
    @TableField("is_leaf")
    @JsonIgnore
    private Boolean isLeaf;

    @ApiModelProperty("是否缓存该页面:    1:是   0:不是")
    @TableField("keep_alive")
    @JsonIgnore
    private Boolean keepAlive;

    @ApiModelProperty("是否隐藏路由: 0否,1是")
    @JsonIgnore
    @TableField("hidden")
    private Integer hidden;

    @ApiModelProperty("是否隐藏tab: 0否,1是")
    @JsonIgnore
    @TableField("hide_tab")
    private Integer hideTab;

    @ApiModelProperty("描述")
    @JsonIgnore
    @TableField("description")
    private String description;

    @ApiModelProperty("删除状态 0正常 1已删除")
    @TableField("logic_flag")
    @JsonIgnore
    private Integer logicFlag;

    @ApiModelProperty("按钮权限状态(0无效1有效)")
    @TableField("status")
    @JsonIgnore
    private String status;

    @ApiModelProperty("外链菜单打开方式 0/内部打开 1/外部打开")
    @TableField("internal_or_external")
    @JsonIgnore
    private Boolean internalOrExternal;

    @TableField(exist=false)
    private List<SysPermissionEntity> child=new ArrayList<>();

    @ApiModelProperty("高度 ,默认40")
    @TableField("height")
    private String height;

    @ApiModelProperty("0 代表false 1 代表true 默认0")
    @TableField("active")
    private Boolean active;

}
