package com.hyl.api.system.entity.business;

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

/**
 * <p>
 * 客户信息表
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_customer_info")
@ApiModel(value = "MCustomerInfoEntity对象", description = "客户信息表")
public class MCustomerInfoEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("客户名称")
    @TableField("username")
    private String username;

    @ApiModelProperty("性别(1-男,0-女)")
    @TableField("sex")
    private Boolean sex;

    @ApiModelProperty("生日")
    @TableField("birthday")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime birthday;

    @ApiModelProperty("电子邮件")
    @TableField("email")
    private String email;

    @ApiModelProperty("电话")
    @TableField("tel")
    private String tel;

    @ApiModelProperty("地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("会员卡号")
    @TableField("membership")
    private String membership;

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

}
