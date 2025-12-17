package com.org.hosply360.util.encryptionUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGO = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    private static SecretKeySpec getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGO);
    }

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        } catch (Exception ex) {
            throw new RuntimeException("Encryption error", ex);
        }
    }

    public static String decrypt(String cipherText) {
        try {
            if (cipherText == null || cipherText.isEmpty()) {
                return cipherText;
            }

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);

            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(decodedBytes));

        } catch (Exception ex) {

            return cipherText;
        }
    }


}
