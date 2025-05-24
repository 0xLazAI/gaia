package com.lazai.utils;

import com.lazai.exception.DomainException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesUtils {

    private static final String ALGORITHM = "AES";

    // 16字节密钥（128位），必须固定长度：16、24、32（对应 AES-128/192/256）
    private static final String SECRET_KEY = "mySecretKey12345";

    // 加密方法
    public static String encrypt(String plainText) {
        try{
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM); // 默认使用 ECB 模式 + PKCS5Padding
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }catch (Throwable t){
            throw new DomainException("encrypt failed!", 500);
        }

    }

    // 解密方法
    public static String decrypt(String cipherText){
        try{
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        }catch (Throwable t){
            throw new DomainException("decrypt failed!", 500);
        }
    }
}