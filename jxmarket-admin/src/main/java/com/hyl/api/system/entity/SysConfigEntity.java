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
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_config")
@ApiModel(value = "SysConfigEntity对象", description = "")
public class SysConfigEntity extends BaseEntity {

    @ApiModelProperty("配置key")
    @TableField("config_key")
    private String configKey;

    @ApiModelProperty("配置value")
    @TableField("config_value")
    private String configValue;

    @ApiModelProperty("中文备注")
    @TableField("remark_en")
    private String remarkEn;

    @ApiModelProperty("英文备注")
    @TableField("remark_zh")
    private String remarkZh;


}
