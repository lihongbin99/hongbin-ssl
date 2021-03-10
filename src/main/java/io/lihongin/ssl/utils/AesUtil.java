package io.lihongin.ssl.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AesUtil {

    private final static String AES = "AES";
    private final static String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String encrypt(String input, String key, String offset) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), AES);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
        byte[] bytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decrypt(byte[] input, String key, String offset) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), AES);
        IvParameterSpec iv = new IvParameterSpec(offset.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, sks, iv);
        byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(input));
        return new String(bytes);
    }

}