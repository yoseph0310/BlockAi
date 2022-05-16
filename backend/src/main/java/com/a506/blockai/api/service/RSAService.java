package com.a506.blockai.api.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

@Service
public class RSAService {

    static final int KEY_SIZE = 1024;

    public HashMap<String, String> createKeypairAsString() {
        HashMap<String, String> stringKeypair = new HashMap<>();
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE, secureRandom);
//            KeyPair keyPair = keyPairGenerator.genKeyPair();
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            stringKeypair.put("publicKey", stringPublicKey);
            stringKeypair.put("privateKey", stringPrivateKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringKeypair;
    }

    /**
     * 암호화
     */
    public String encode(String plainData, String stringPublicKey) {
        String encryptedData = null;
        try {
            //평문으로 전달받은 공개키를 공개키객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            //만들어진 공개키객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // plainData 출력
            System.out.println("RSAService <encode> plainData : " + plainData);
            // plainData getBytes 출력
            System.out.println("RSAService <encode> plainData.getBytes.length : " + plainData.getBytes(StandardCharsets.UTF_8).length);
            // byteEncryptedData 출력
            //평문을 암호화하는 과정
            byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < byteEncryptedData.length; i++){
                System.out.println("RSAService <encode> byteEncryptedData : " + byteEncryptedData[i]);
            }
            // encryptedData 출력
            encryptedData = Base64.getEncoder().encodeToString(byteEncryptedData);
            System.out.println("RSAService <encdoe> encryptedData : " + encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    /**
     * 복호화
     */
    public String decode(String encryptedData, String stringPrivateKey) {
        String decryptedData = null;
        System.out.println("RSAService <decode> encryptedData : " + encryptedData);
        System.out.println("RSAService <decode> stringPrivateKey : " + stringPrivateKey);
        try {
            //평문으로 전달받은 개인키를 개인키객체로 만드는 과정
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //만들어진 개인키객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            //암호문을 평문화하는 과정
            byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes(StandardCharsets.UTF_8));
//            for(int i = 0; i < byteEncryptedData.length; i++ ){
//                System.out.println("RSAService <decode> byteEncryptedData : " + byteEncryptedData[i]);
//            }

            System.out.println("RSAService <decode> byteEncryptedData length : " + byteEncryptedData.length);
            byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
            decryptedData = new String(byteDecryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedData;
    }
}
