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
 * 商品库存表
 * </p>
 *
 * @author AnneHan
 * @since 2023-10-19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("m_good_quantity")
@ApiModel(value = "MGoodQuantityEntity对象", description = "商品库存表")
public class MGoodQuantityEntity {

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("当前库存数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty("当前库位商品库存数量")
    @TableField("inner_quantity")
    private Integer innerQuantity;

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

    @ApiModelProperty("商品名称")
    @TableField(exist = false)
    private String goodName;
    @ApiModelProperty("商品编号")
    @TableField(exist = false)
    private String goodId;

}
