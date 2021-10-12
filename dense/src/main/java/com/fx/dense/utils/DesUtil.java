package com.fx.dense.utils;

import com.fx.dense.base.Const;
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
 * @program: pkc
 * @description: des加密类
 * @author: WangHaiMing
 * @create: 2021-10-08 16:00
 **/
@Component
public class DesUtil {

    /**
     * • ECB：Electronic Code Book（电子码本模式）
     * • CBC：Cipher Block Chaining（密码块链模式）
     * • CTR：Counter（计数器模式）
     *
     * 此外，还有其他的一些模式，本文中将不做介绍。
     * • CFB：Cipher Feedback（密码反馈模式）
     * • OFB：Output Feedback（输出反馈模式）
     */
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final String ALGORITHM_RSA = "RSA";

    private static final String fill_pkc7 = "PKCS7Padding";

    private static final String fill_pkc5 = "PKCS5Padding";

    private static final String DOES_NOT_SUPPORT_IV = "DES/ECB/PKCS5Padding";

    private static final String mode_ecb = "ECB";


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

            //  这种模式不可用（iv 自定义参数）
            if(mode_ecb.equals(model.getEncryptionMode())){
                if(model.getIsItEncrypted() == Const.ENCRYPT_OR_DECRYPT[0]){
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    byte[] doFinal = cipher.doFinal(model.getContext().getBytes(model.getCharacterSet()));
                    if (model.getOutput().equals(Const.Base64)) {
                        BASE64Encoder encoder = new BASE64Encoder();
                        String encode = encoder.encode(doFinal);
                        return encode;
                    }
                    return toHexString(doFinal);
                }else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                    byte[] bytes = convertHexString(model.getContext());
                    byte[] retByte = cipher.doFinal(bytes);
                    if (model.getOutput().equals(Const.Base64)) {
                        BASE64Encoder encoder = new BASE64Encoder();
                        String encode = encoder.encode(retByte);
                        return encode;
                    }
                    return new String(retByte);
                }

            }else{
                IvParameterSpec iv = new IvParameterSpec(ArrayUtil.byte16CompletionIv(model.getIvOffset(),model.getCharacterSet(),Const.LENGTH_OF_THE_COMPLETION_BYTE[0]));
                if(model.getIsItEncrypted() == Const.ENCRYPT_OR_DECRYPT[0]){
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                    byte[] doFinal = cipher.doFinal(model.getContext().getBytes(model.getCharacterSet()));
                    if (model.getOutput().equals(Const.Base64)) {
                        BASE64Encoder encoder = new BASE64Encoder();
                        String encode = encoder.encode(doFinal);
                        return encode;
                    }
                    return toHexString(doFinal);
                }else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                    byte[] bytes = convertHexString(model.getContext());
                    byte[] retByte = cipher.doFinal(bytes);
                    if (model.getOutput().equals(Const.Base64)) {
                        BASE64Encoder encoder = new BASE64Encoder();
                        String encode = encoder.encode(retByte);
                        return encode;
                    }
                    return new String(retByte);
                }
            }
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
