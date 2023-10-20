package com.hyl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 对称加密工具类
 *
 * @author AnneHan
 * @date 2023-09-15
 */
public class AesEncodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(AesEncodeUtil.class);

    /**
     * 偏移量 AES 为16bytes. DES 为8bytes
     */
    public static final String VIP_ARA = "";


    /**
     * 填充类型  GCM
     */
    public static final String AES_TYPE = "AES/CBC/PKCS5Padding";

    /**
     * //public static final String AES_TYPE = "AES/ECB/PKCS7Padding";
     * //此类型 加密内容,密钥必须为16字节的倍数位,否则抛异常,需要字节补全再进行加密
     * //public static final String AES_TYPE = "AES/ECB/NoPadding";
     * //java 不支持ZeroPadding
     * //public static final String AES_TYPE = "AES/CBC/ZeroPadding";
     * //私钥  AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。
     */
    private static final String AES_KEY = "";


    /**
     * 加密
     *
     * @param cleartext 加密内容
     * @return 加密密文
     */
    public static String encrypt(String cleartext) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIP_ARA.getBytes(StandardCharsets.UTF_8));
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            //实例化加密类，参数为加密方式，要写全
            //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            //初始化，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec
            //CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            //加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式
            byte[] encryptedData = cipher.doFinal(cleartext.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(encryptedData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("加密失败：{}", e.getMessage());
            return "";
        }
    }

    /**
     * 解密
     *
     * @param encrypted 解密密文
     * @return 解密内容
     */
    public static String decrypt(String encrypted) {
        try {
            encrypted = encrypted.replaceAll(" ", "+");
            byte[] byteMi = Base64.getDecoder().decode(encrypted);
            IvParameterSpec zeroIv = new IvParameterSpec(VIP_ARA.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec key = new SecretKeySpec(
                    AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(AES_TYPE);
            //与加密时不同MODE:Cipher.DECRYPT_MODE
            //CBC类型的可以在第三个参数传递偏移量zeroIv,ECB没有偏移量
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("解密失败：{}", e.getMessage());
            return "";
        }
    }


    /**
     * 测试
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String s = "";
        System.out.println( decrypt(s));
    }
}
