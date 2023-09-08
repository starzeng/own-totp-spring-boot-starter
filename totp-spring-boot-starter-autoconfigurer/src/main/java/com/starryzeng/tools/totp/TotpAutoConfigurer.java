package com.starryzeng.tools.totp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 曾危雄
 * @version 1.0.0
 * @date 2023/9/8 14:41
 */
@Configuration
@ConditionalOnProperty(value = "totp.key")
@EnableConfigurationProperties(TotpProperties.class)
public class TotpAutoConfigurer {
    
    @Autowired
    TotpProperties totpProperties;
    
    
    

}
