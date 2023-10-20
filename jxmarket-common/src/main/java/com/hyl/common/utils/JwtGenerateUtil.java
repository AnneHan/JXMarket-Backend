package com.hyl.common.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.alibaba.fastjson.JSONObject;
import com.hyl.common.constants.GlobalConstant;
import com.hyl.common.domain.JwtData;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * jwt产生实效
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Slf4j
public class JwtGenerateUtil {

    /**
     * 私钥
     */
    private static final String TOKEN_SECRET = "";

    /**
     * 生成token
     */
    public static String createToken(JwtData jwtData, Date expireDate) {
        Date currentDate = new Date();
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.ISSUED_AT, currentDate);
        payload.put(JWTPayload.EXPIRES_AT, expireDate);
        payload.put(JWTPayload.NOT_BEFORE, currentDate);
        payload.put(JWTPayload.JWT_ID, UUID.randomUUID().toString());
        payload.put(GlobalConstant.JWT_DATA_KEY, JSONObject.toJSONString(jwtData));
        return JWTUtil.createToken(payload, TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验token是否过期
     */
    public static boolean verifyIsExpired(String token, Date verifyDate) {
        boolean flag = true;
        try {
            JWTValidator.of(token).validateDate(verifyDate);
        } catch (ValidateException e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 校验token是否正确
     */
    public static boolean verify(String token) {
        return JWTUtil.verify(token, TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取jwt claim 参数
     */
    public static String getJwtParams(String key, String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return jwt.getPayload(key).toString();
    }

}
