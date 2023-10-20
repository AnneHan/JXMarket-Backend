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
 * 采购明细表
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_purchase_detail")
@ApiModel(value = "MPurchaseDetailEntity对象", description = "采购明细表")
public class MPurchaseDetailEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("采购单编号")
    @TableField("purchase_id")
    private String purchaseId;

    @ApiModelProperty("商品编号")
    @TableField("good_id")
    private String goodId;

    @ApiModelProperty("商品名称")
    @TableField(exist = false)
    private String goodName;

    @ApiModelProperty("数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty("单价")
    @TableField("unit_price")
    private BigDecimal unitPrice;

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
