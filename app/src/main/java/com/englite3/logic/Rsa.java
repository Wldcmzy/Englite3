package com.englite3.logic;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import android.util.Base64;


import javax.crypto.Cipher;

public class Rsa {
    private static String publickey;
    private static final String algorithm = "RSA/ECB/PKCS1Padding";

    public Rsa(String publickey){
        this.publickey = publickey;

    }

    public static PublicKey getPublicKey(byte[] keyBytes) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static byte[] encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publickey.getBytes()));
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
        return ciphertext;
    }
}
