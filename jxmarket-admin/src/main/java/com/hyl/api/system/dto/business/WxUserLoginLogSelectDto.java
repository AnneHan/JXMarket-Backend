package com.hyl.api.system.dto.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author AnneHan
 * @date 2023-09-15
 */
@Data
public class WxUserLoginLogSelectDto {

    private Long acskey;

    private String appCode;

    private String deviceType;

    private String imei;

    private String imsi;

    private String [] logindate;

    private String sysVersion;

    private String userName;

    private String version;

    @ApiModelProperty(value = "每页记录数")
    private int pageSize;

    @ApiModelProperty(value = "页数,最小为1")
    private int pages;
}
