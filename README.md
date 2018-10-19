# AndroidKeyStore
android KeyStore demo

对于高版本Android使用安全性更高的AndroidKeyStore保存本地密钥兼容至Andorid4.0

三个module：
- app
- KeyStoreDemo
- CryptUtils

其中CryptUtils兼容sdk14到最新到版本使用android自带的KeyStore实现安全的保存本地密钥。实现的主要方式是：
1. 在android6.0(SDK>=23)及以上情况下使用AndroidKeyStore实现类似苹果keychain的独立于app外的硬件加密存储密钥。
2. 在小于android6.0(SDK<23)情况下，使用传统的KeyStore实现加密存储密钥。

在android4.3和android8.0上面经过实测，可以在手机重启后还原出本地保存的密钥。

>**注意**：对于SecretKey对象，处于安全考虑，在android上无法通过getEncoded()获取字节数组从而得到加密密钥。
