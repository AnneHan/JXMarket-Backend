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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商品信息表
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_good_info")
@ApiModel(value = "MGoodInfoEntity对象", description = "商品信息表")
public class MGoodInfoEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("商品类别")
    @TableField("category")
    private String category;

    @ApiModelProperty("规格型号")
    @TableField("model")
    private String model;

    @ApiModelProperty("单位")
    @TableField("unit")
    private String unit;

    @ApiModelProperty("成本价")
    @TableField("cost_price")
    private BigDecimal costPrice;

    @ApiModelProperty("零售价")
    @TableField("retail_price")
    private BigDecimal retailPrice;

    @ApiModelProperty("保质期")
    @TableField("expiry_date")
    private LocalDateTime expiryDate;

    @ApiModelProperty("条形码")
    @TableField("bar_code")
    private String barCode;

    @ApiModelProperty("所属供应商")
    @TableField("supplier_id")
    private String supplierId;

    @ApiModelProperty("所属供应商名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty("库存预警值")
    @TableField("warn_value")
    private Integer warnValue;

    @ApiModelProperty("所在货架号")
    @TableField("shelf_number")
    private String shelfNumber;

    @ApiModelProperty("库位库存预警值")
    @TableField("inner_warn_value")
    private Integer innerWarnValue;

    @ApiModelProperty("备注")
    @TableField("notes")
    private String notes;

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
