package com.hyl.common.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * jwt数据
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
public class JwtData {
    /** 账户 */
    private String account;
    /** 语言 */
    private String language;
}
