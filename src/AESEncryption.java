import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;

/**
 * This class contains the encrypt and decrypt functions
 */
public class AESEncryption {
    static String IVector = "AAAAAAAAAAAAAAAA";
    
    public static byte[] encrypt(String unencryptedString, String encryptionKey) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IVector.getBytes("UTF-8")));
        return c.doFinal(unencryptedString.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] encryptedBytes, String encryptionKey) throws Exception {
        Cipher c = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IVector.getBytes("UTF-8")));
        return new String(c.doFinal(encryptedBytes), "UTF-8");
    }
}