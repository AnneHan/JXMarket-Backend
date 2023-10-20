package com.hyl.api.system.vo.business;

import lombok.Data;

import java.util.List;

/**
 * @author AnneHan
 * @date 2023-09-15
 */
@Data
public class RespDataVo<T> {
    private List<T> list ;
    private String size ;
}
