package com.starryzeng.tools.totp;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.HexUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * TOTP验证码生成工具类
 *
 * @author 曾危雄
 * @version 1.0.0
 * @date 2023/9/7 20:50
 */
public class TOTP {
    
    /**
     * 时间前后偏移量
     * <p>
     * 用于防止客户端时间不精确导致生成的TOTP与服务器端的TOTP一直不一致
     * <p>
     * 如果为0,当前时间为 10:10:15
     * <p>
     * 则表明在 10:10:00-10:10:30 之间生成的TOTP 能校验通过
     * <p>
     * 如果为1,则表明在
     * <p>
     * 10:09:30-10:10:00
     * <p>
     * 10:10:00-10:10:30
     * <p>
     * 10:10:30-10:11:00
     * <p>
     * 之间生成的TOTP 能校验通过
     * <p>
     * 以此类推
     */
    private static final int WINDOW_SIZE = 0;
    
    /**
     * 加密算法 (HmacSHA1，HmacSHA256，HmacSHA512)
     */
    private static final String CRYPTO = "HmacSHA1";
    
    /**
     * 返回位数
     */
    private static final int RETURN_DIGITS = 6;
    
    /**
     * 0 1 2 3 4 5 6 7 8
     */
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
    
    /**
     * 进步30秒
     */
    private static final long TIME_STEP = 30L;
    
    /**
     * 空字符串
     */
    private static final String NULL_STR = "";
    
    /**
     * 空格字符串
     */
    private static final String SPACE_STR = " ";
    
    /**
     * 生成大写密钥
     *
     * @return 秘钥
     */
    public static String getSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        String secretKey = Base32.encode(bytes);
        return secretKey.toUpperCase();
    }
    
    /**
     * 使用JCE提供加密算法。
     * <p>
     * HMAC使用加密哈希算法作为参数计算哈希消息认证码。
     *
     * @param keyBytes 用于HMAC密钥的字节
     * @param text     要认证的消息或文本
     */
    private static byte[] hmacSha(byte[] keyBytes, byte[] text) {
        try {
            Mac hmac = Mac.getInstance(CRYPTO);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }
    
    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param hexStr 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexStr2Bytes(String hexStr) {
        byte[] bArray = new BigInteger("10" + hexStr, 16).toByteArray();
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }
    
    /**
     * 生成一个TOTP值。
     *
     * @param secretKey 秘钥
     * @return 基数10中的数字字符串
     */
    public static String generateTotp(String secretKey) {
        String base32Key = secretKey.replace(SPACE_STR, NULL_STR).toUpperCase();
        String hexKey = HexUtil.encodeHexStr(Base32.decode(base32Key));
        String hexTime = Long.toHexString(Instant.now().getEpochSecond() / TIME_STEP);
        
        StringBuilder timeBuilder = new StringBuilder(hexTime);
        while (timeBuilder.length() < 16) {
            timeBuilder.insert(0, "0");
        }
        hexTime = timeBuilder.toString();
        
        byte[] msg = hexStr2Bytes(hexTime);
        byte[] keyBytes = hexStr2Bytes(hexKey);
        byte[] hash = hmacSha(keyBytes, msg);
        
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24) //
                | ((hash[offset + 1] & 0xff) << 16) //
                | ((hash[offset + 2] & 0xff) << 8) //
                | (hash[offset + 3] & 0xff);
        int otp = binary % DIGITS_POWER[RETURN_DIGITS];
        
        return String.format("%0" + RETURN_DIGITS + "d", otp);
    }
    
    /**
     * 根据时间偏移量计算哈希值
     *
     * @param base32secretKey base32 的 secretKey
     * @param timeOffset      时间偏移量
     * @return 哈希值
     * @throws NoSuchAlgorithmException 没有这样的算法例外
     * @throws InvalidKeyException      无效密钥异常
     */
    private static long getTruncatedHash(byte[] base32secretKey, long timeOffset)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        for (int i = 8; i-- > 0; timeOffset >>>= 8) {
            data[i] = (byte) timeOffset;
        }
        SecretKeySpec signKey = new SecretKeySpec(base32secretKey, CRYPTO);
        Mac mac = Mac.getInstance(CRYPTO);
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return truncatedHash;
    }
    
    /**
     * 检验 code 是否正确
     *
     * @param secret 密钥
     * @param code   code
     * @return true|false
     */
    public static boolean verifyCode(String secret, String code) {
        byte[] base32secretKey = Base32.decode(secret);
        long longCode = Long.parseLong(code);
        long timeOffset = Instant.now().getEpochSecond() / TIME_STEP;
        long hash;
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
            try {
                hash = getTruncatedHash(base32secretKey, timeOffset + i);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            if (hash == longCode) {
                return true;
            }
        }
        return false;
    }
    
    
}
