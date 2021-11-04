package com.fx.dense.utils;

import com.fx.dense.exception.BusinessException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.fx.dense.utils.DesUtil.base642Byte;
import static com.fx.dense.utils.DesUtil.byte2Base64;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-04 16:25
 **/
public class RsaUtils {


    private static final String ALGORITHM_RSA = "RSA";


    /***
     * 生成秘钥对
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() {

        try {
            //创建密钥对KeyPair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
            //密钥长度推荐为1024位
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 获取公钥(Base64编码)
     * @param keyPair
     * @return
     */
    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }

    /**
     * 获取私钥(Base64编码)
     * @param keyPair
     * @return
     */
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }

    /**
     * 将Base64编码后的公钥转换成PublicKey对象
     * @param publicKey
     * @return
     */
    public static PublicKey getPublicKey(String publicKey) {
        try {
            byte[] keyBytes = base642Byte(publicKey.replaceAll(" +",""));
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     * @param privateKey
     * @return
     */
    public static PrivateKey getPrivateKey(String privateKey) {
        try {
            byte[] keyBytes = base642Byte(privateKey.replaceAll(" +",""));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec (keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * RSA公钥加密
     * @param content
     * @return
     */
    public static byte[] publicEncrypt(byte[] content, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * RSA私钥解密
     * @param content
     * @return
     */
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

}
