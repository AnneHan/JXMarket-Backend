package com.hyl.api.system.dto.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author AnneHan
 * @date 2023-09-15
 */
@Data
public class WxUserDealDto {

    private String appVersion;

    private Long binding;

    private String clientNo;

    private String content;

    private String createTime;

    private String employeeId;

    private Long id;

    private String language;

    private String loginStatus;

    private String loginTime;

    private String openid;

    private String origin;

    private String originCode;

    private String registrationId;

    private String underwriterId;

    private String source;

    @ApiModelProperty(value = "每页记录数")
    private int pageSize;

    @ApiModelProperty(value = "页数,最小为1")
    private int pages;
}
