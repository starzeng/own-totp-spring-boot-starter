# TOTP使用

## 1.TOTP是什么

**time-based one-time passwords (TOTP)** 基于时间戳算法的一次性密码生成器， 规范见：[RFC 6238](https://tools.ietf.org/html/rfc6238) .
是一种基于时间的一次性密码，用于身份验证和安全认证。它通过使用用户设备上的当前时间和预先生成的密钥来生成一个动态的、一次性的密码，每个密码只能使用一次，确保了安全性和可靠性。、

## 2.Google Authenticator

是一款开源的双因素认证（**2FA**）应用程序，它使用 **Time-based One-Time Passwords (TOTP)** 算法来生成动态的、一次性的密码，用于身份验证和安全认证。它可以用于许多网站和服务，如 Gmail、Dropbox、GitHub 等，可以有效防止黑客攻击和账户被盗用。Google Authenticator 可以在 Android 和 iOS 设备上使用，也可以使用其他支持 TOTP 算法的应用程序。

Chrome下载地址：https://github.com/Authenticator-Extension/Authenticator

Android下载地址：https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2

iPhone 和 iPad下载地址：https://apps.apple.com/app/google-authenticator/id388497605

# 3.totp-spring-boot-starter使用

**添加依赖**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>com.starryzeng.tools</groupId>
    <artifactId>totp-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>

<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>
```

**例子：**

```java
    /**
     * 测试用, 实际情况存储在数据库
     */
    private String secretKeyCache = "";
    
    /**
     * 生成二维码，APP直接扫描绑定，两种方式任选一种
     *
     * @param account  名称
     * @param issuer   发行人
     * @param response {@link HttpServletResponse}
     */
    @GetMapping("/getQrCode")
    public void getQrcode(String account, String issuer, HttpServletResponse response) throws IOException {
        // 获取32位密钥
        String secretKey = GoogleAuthenticator.getSecretKey();
        
        // 缓存密钥,用于验证,正常存储用户数据表
        secretKeyCache = secretKey;
        
        // 生成二维码内容
        String qrCodeText = GoogleAuthenticator.getQrCodeText(secretKey, account, issuer);
        
        // 生成二维码输出
        QrCodeUtil.generate(qrCodeText, 200, 200, ImgUtil.IMAGE_TYPE_PNG, response.getOutputStream());
    }
    
    /**
     * 验证 code 是否正确
     */
    @GetMapping("/checkCode")
    public String checkCode(String code) {
        String newCode = GoogleAuthenticator.getCode(secretKeyCache);
        System.out.println(code + "----" + newCode);
        if (GoogleAuthenticator.verifyCode(secretKeyCache, code)) {
            return "success";
        }
        return "fail";
    }
```









