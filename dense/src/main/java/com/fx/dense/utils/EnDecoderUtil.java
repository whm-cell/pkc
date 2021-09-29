package com.fx.dense.utils;

import com.fx.dense.error.ErrorMessage;
import com.fx.dense.exception.BusinessException;
import com.fx.dense.model.DesRequestModel;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Administrator
 */
@Component
public class EnDecoderUtil {

    private static final String ALGORITHM_RSA = "RSA";
    private static final String ALGORITHM_DES = "DES";
    private static final String ALGORITHM_DES_MODE = "DES/CBC/PKCS5Padding";

    public static String rsaPublicKey;
    public static String rsaPrivateKey;

    public static String desSecretKey;

    @Value("${rsa.publicKey}")
    private void setRsaPublicKey(String publicKey) {
        rsaPublicKey = publicKey;
    }

    @Value("${rsa.privateKey}")
    private void setRsaPrivateKey(String privateKey) {
        rsaPrivateKey = privateKey;
    }

    @Value("${des.secretKey}")
    private void setDesSecretKey(String secretKey) {
        desSecretKey = secretKey;
    }



    public static void main(String[] args) {
        Charset utf8 = StandardCharsets.UTF_8;
        System.out.println(utf8);
    }

    /**
     * des加密
     * @param model
     * @return
     */
    public static String desEncrypt(DesRequestModel model) {

        try{
            Cipher cipher = Cipher.getInstance(model.getBuildMode());
            DESKeySpec desKeySpec = new DESKeySpec(model.getKey().getBytes(model.getCharacterSet()));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(model.getType());
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(model.getKey().getBytes(model.getCharacterSet()));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return toHexString(cipher.doFinal(model.getContext().getBytes(model.getCharacterSet())));
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * des解密
     * @param model
     * @return
     */
    public static String desDecrypt(DesRequestModel model) {
        try{
            byte[] bytesrc = convertHexString(model.getContext());
            Cipher cipher = Cipher.getInstance(model.getEncryptionMode());
            DESKeySpec desKeySpec = new DESKeySpec(model.getKey().getBytes(model.getCharacterSet()));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(model.getType());
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(model.getKey().getBytes(model.getCharacterSet()));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    private static byte[] convertHexString(String ss)
    {
        byte[] digest = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++)
        {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte)byteValue;
        }

        return digest;
    }

    private static String toHexString(byte[] data) {

        StringBuilder hexString = new StringBuilder();
        for (byte item : data) {
            String plainText = Integer.toHexString(0xff & item);
            if (plainText.length() < 2){
                plainText = "0" + plainText;
            }
            hexString.append(plainText);
        }
        return hexString.toString();
    }

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

    /**
     * 字节数组转Base64编码
     * @param bytes
     * @return
     */
    public static String byte2Base64(byte[] bytes){
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * Base64编码转字节数组
     * @param base64Key
     * @return
     * @throws IOException
     */
    public static byte[] base642Byte(String base64Key) {

        try {
            BASE64Decoder decoder = new BASE64Decoder();
            return decoder.decodeBuffer(base64Key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }
}
