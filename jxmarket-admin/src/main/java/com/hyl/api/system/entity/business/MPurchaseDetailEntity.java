package com.hyl.api.system.entity.business;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hyl.core.entity.BaseEntity;
import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
public class MPurchaseDetailEntity extends BaseEntity {

    @ApiModelProperty("采购单编号")
    @TableField("purchase_id")
    private Long purchaseId;

    @ApiModelProperty("商品编号")
    @TableField("good_id")
    private Long goodId;

    @ApiModelProperty("数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty("单价")
    @TableField("unit_price")
    private BigDecimal unitPrice;


}
