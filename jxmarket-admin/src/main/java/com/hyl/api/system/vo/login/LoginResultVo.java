package com.hyl.api.system.vo.login;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录结果
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Getter
@Setter
public class LoginResultVo {
    private String token;
    private String id ;
}
