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
 * 系统日志
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_log")
@ApiModel(value = "SysLogEntity", description = "系统日志表")
public class SysLogEntity extends BaseEntity {



    @ApiModelProperty("日志类型（0登录日志，1操作日志）")
    @TableField("log_type")
    private String logType;


    @ApiModelProperty("日志内容")
    @TableField("log_content")
    private String logContent;

    @ApiModelProperty("操作类型")
    @TableField("operate_type")
    private String operateType;

    @ApiModelProperty("操作用户账号")
    @TableField("userid")
    private String userid;


    @ApiModelProperty("操作用户名称")
    @TableField("username")
    private String username;


    @ApiModelProperty("IP")
    @TableField("ip")
    private String ip;


    @ApiModelProperty("请求java方法")
    @TableField("method")
    private String method;


    @ApiModelProperty("请求路径")
    @TableField("request_url")
    private String requestUrl;


    @ApiModelProperty("请求参数")
    @TableField("request_param")
    private String requestParam;


    @ApiModelProperty("请求类型")
    @TableField("request_type")
    private String requestType;


    @ApiModelProperty("耗时")
    @TableField("cost_time")
    private long costTime;

}
