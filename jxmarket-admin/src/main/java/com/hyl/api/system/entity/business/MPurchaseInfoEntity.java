package com.hyl.api.system.entity.business;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 采购信息表
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_purchase_info")
@ApiModel(value = "MPurchaseInfoEntity对象", description = "采购信息表")
public class MPurchaseInfoEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("供应商编号")
    @TableField("supplier_id")
    private String supplierId;

    @ApiModelProperty("供应商名称")
    @TableField(exist = false)
    private String supplierName;

    @ApiModelProperty("仓库编号")
    @TableField("deport_id")
    private String deportId;

    @ApiModelProperty("采购日期")
    @TableField("purchase_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime purchaseDate;

    @ApiModelProperty("金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty("付款方式")
    @TableField("pay_method")
    private String payMethod;

    @ApiModelProperty("付款状态")
    @TableField("pay_status")
    private String payStatus;

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

    @ApiModelProperty("更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty("创建人")
    @TableField("create_by")
    private String createBy;

}
