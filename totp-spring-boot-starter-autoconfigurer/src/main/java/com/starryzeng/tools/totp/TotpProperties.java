package com.starryzeng.tools.totp;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 曾危雄
 * @version 1.0.0
 * @date 2023/9/8 14:35
 */
@ConfigurationProperties("totp")
public class TotpProperties {
    
    private String key;
    
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
}
