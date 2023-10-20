package com.hyl.common.utils;

import com.hyl.common.constants.GlobalConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

/**
 * HmacSHA1加密类
 *
 * @author AnneHan
 * @date 2023-09-15
 */
@Slf4j
public class HmacSha256Util {


    private static final long MAX_EXPIRE = 60 * 5L;

    private static final String SECRET = "";

    private static final String ALGORITHM = "HmacSHA256";

    public static String getHmacSHA256(String content) {
        Mac sha256_HMAC;
        try {
            sha256_HMAC = Mac.getInstance(ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            sha256_HMAC.init(secret_key);
            return bytesToHexString(sha256_HMAC.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("getHmacSHA256加密失败，content：{}", content);
        }
        return null;
    }

    /**
     * Convert byte[] to hex string
     *
     * @param src byte[] data
     * @return hex string
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 验证签名
     */
    public static String signature(String timestamp, String nonce, String content) {
        if (StringUtils.isEmpty(timestamp)
                || StringUtils.isEmpty(nonce)
                || StringUtils.isEmpty(content)) {
            log.error("生成签名参数不能为空!,签名参数:timestamp:->{},nonce->{},content->{}", timestamp, nonce, content);
            return null;
        }
        String params = timestamp + "&" + nonce + "&" + content;
        String signature = null;
        try {
            signature = getHmacSHA256(params);
        } catch (Exception e) {
            log.error("signature error.", e);
        }
        return signature;
    }

    public static boolean verifySignature(
            String signature, String timestamp, String nonce, String content) {
        String sign = signature(timestamp, nonce, content);
        if (StringUtils.isEmpty(sign)) {
            return false;
        }
        return signature.equals(sign);
    }

    public static void main(String[] args) {
        signature(System.currentTimeMillis() + "", UUID.randomUUID().toString(), "123");
    }

    /**
     * 验证参数
     *
     * @param paramsMap
     */
    public static void validateParams(Map<String, String> paramsMap) {
        Assert.hasText(paramsMap.get(GlobalConstant.SIGN_APP_ID_KEY), "签名验证失败:APP_ID不能为空");
        Assert.hasText(paramsMap.get(GlobalConstant.SIGN_NONCE_KEY), "签名验证失败:NONCE不能为空");
        Assert.hasText(paramsMap.get(GlobalConstant.SIGN_TIMESTAMP_KEY), "签名验证失败:TIMESTAMP不能为空");
        Assert.hasText(paramsMap.get(GlobalConstant.SIGN_SIGN_TYPE_KEY), "签名验证失败:SIGN_TYPE不能为空");
        Assert.hasText(paramsMap.get(GlobalConstant.SIGN_SIGN_KEY), "签名验证失败:SIGN不能为空");

        try {
            HylDateUtils.parseDate(paramsMap.get(GlobalConstant.SIGN_TIMESTAMP_KEY), "yyyyMMddHHmmss");
        } catch (ParseException e) {
            throw new IllegalArgumentException("签名验证失败:TIMESTAMP格式必须为:yyyyMMddHHmmss");
        }
        String timestamp = paramsMap.get(GlobalConstant.SIGN_TIMESTAMP_KEY);
        long clientTimestamp = Long.parseLong(timestamp);
        //判断时间戳 timestamp=201808091113
        if ((HylDateUtils.getCurrentTimestamp() - clientTimestamp) > MAX_EXPIRE) {
            throw new IllegalArgumentException("签名验证失败:TIMESTAMP已过期");
        }
    }

}
