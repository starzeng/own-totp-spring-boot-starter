# own-totp-spring-boot-starter
# TOTP使用

## 1.TOTP是什么

**time-based one-time passwords (TOTP)** 基于时间戳算法的一次性密码生成器， 规范见：[RFC 6238](https://tools.ietf.org/html/rfc6238) .
是一种基于时间的一次性密码，用于身份验证和安全认证。它通过使用用户设备上的当前时间和预先生成的密钥来生成一个动态的、一次性的密码，每个密码只能使用一次，确保了安全性和可靠性。、

## 2.Google Authenticator

是一款开源的双因素认证（**2FA**）应用程序，它使用 **Time-based One-Time Passwords (TOTP)** 算法来生成动态的、一次性的密码，用于身份验证和安全认证。它可以用于许多网站和服务，如 Gmail、Dropbox、GitHub 等，可以有效防止黑客攻击和账户被盗用。Google Authenticator 可以在 Android 和 iOS 设备上使用，也可以使用其他支持 TOTP 算法的应用程序。

Chrome下载地址：https://github.com/Authenticator-Extension/Authenticator

Android下载地址：https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2

iPhone 和 iPad下载地址：https://apps.apple.com/app/google-authenticator/id388497605
