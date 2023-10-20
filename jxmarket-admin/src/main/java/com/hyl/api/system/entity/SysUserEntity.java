package com.hyl.api.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户表
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_user")
@ApiModel(value = "SysUserEntity对象", description = "系统用户表")
public class SysUserEntity {


    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("登录账号")
    @TableField("username")
    private String username;

    @ApiModelProperty("密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @ApiModelProperty("头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty("生日")
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime birthday;

    @ApiModelProperty("性别(1-男,0-女)")
    @TableField("sex")
    private Boolean sex;

    @ApiModelProperty("电子邮件")
    @TableField("email")
    private String email;

    @ApiModelProperty("电话")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("状态(1-正常,0-冻结)")
    @TableField("status")
    private Boolean status;

    @ApiModelProperty("删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    @TableLogic
    private Boolean delFlag;

    @TableField(fill = FieldFill.INSERT)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @JsonIgnore
    private String updateBy;

    @JsonIgnore
    private String createBy;

    /**
     * 角色ID列表
     */
    @TableField(exist = false)
    @JsonIgnore
    private List<Long> roleIdList;

    /**
     * 角色信息
     */
    @TableField(exist = false)
    private SysRoleEntity role;

    @ApiModelProperty("个人描述")
    @TableField("personal_description")
    private String personalDescription;
}
