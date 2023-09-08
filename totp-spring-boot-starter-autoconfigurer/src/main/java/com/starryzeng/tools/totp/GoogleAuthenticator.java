package com.starryzeng.tools.totp;

import cn.hutool.core.util.StrUtil;
import org.springframework.util.StringUtils;

/**
 * @author 曾危雄
 * @version 1.0.0
 * @date 2023/9/7 19:37
 */
public class GoogleAuthenticator {
    
    /**
     * Google Authenticator 二维码链接
     */
    private static final String GA_URL = "otpauth://totp/{}?secret={}";
    
    /**
     * 生成大写的密钥
     *
     * @return 秘钥
     */
    public static String getSecretKey() {
        return TOTP.getSecretKey();
    }
    
    /**
     * 生成二维码内容
     *
     * @param secretKey 密钥
     * @param account   账户名
     * @param issuer    网站地址（可不写）
     * @return 生成二维码内容
     */
    public static String getQrCodeText(String secretKey, String account, String issuer) {
        secretKey = secretKey.replace(" ", "").toUpperCase();
        String prefix = (StringUtils.hasLength(issuer) ? (issuer + ":") : "") + account;
        String suffix = secretKey + (StringUtils.hasLength(issuer) ? ("&issuer=" + issuer) : "");
        return StrUtil.format(GA_URL, prefix, suffix);
    }
    
    /**
     * 获取验证码
     *
     * @param secretKey 秘钥
     * @return 一次性验证码
     */
    public static String getCode(String secretKey) {
        return TOTP.generateTotp(secretKey);
    }
    
    /**
     * 检验 code 是否正确
     *
     * @param secret 密钥
     * @param code   code
     * @return true|false
     */
    public static boolean verifyCode(String secret, String code) {
        return TOTP.verifyCode(secret, code);
    }
    
}
