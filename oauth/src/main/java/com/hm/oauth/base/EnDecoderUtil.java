package com.hm.oauth.base;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 加密解密工具类
 * @author zl
 * @date 2020/8/20 10:57
 */
public class EnDecoderUtil {

    private static final String ALGORITHM_RSA = "RSA";
    private static final String ALGORITHM_DES = "DES";
    private static final String ALGORITHM_DES_MODE = "DES/CBC/PKCS5Padding";

    /**
     * des加密
     * @param message
     * @param key
     * @return
     */
    public static String desEncrypt(String message, String key) {

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES_MODE);
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return toHexString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        }catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    /**
     * des解密
     * @param message
     * @param key
     * @return
     */
    public static String desDecrypt(String message, String key) {
        try{
            byte[] bytesrc = convertHexString(message);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES_MODE);
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
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
        }
        return null;
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
        }
        return null;
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
        }
        return null;
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
        }
        return null;
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
        }
        return null;
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
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        try {


            /*String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCs+j6BWCUcrOIrVqIYL0+ySismH8aFplS3kdtbjec/ANQwcR2NdC3Rn8kKyUuVPFjaze9wro9VY7VzGHENQhSrO2Y92qvjcUsputVV7z1VhIxDduJnjuz+6iEBScRJjzvvrF5S16YfPfFKg5OONrJ2x5p3zliHoaX2C1HBIvlGowIDAQAB";
            String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKz6PoFYJRys4itWohgvT7JKKyYf\n" +
                    "xoWmVLeR21uN5z8A1DBxHY10LdGfyQrJS5U8WNrN73Cuj1VjtXMYcQ1CFKs7Zj3aq+NxSym61VXv\n" +
                    "PVWEjEN24meO7P7qIQFJxEmPO++sXlLXph898UqDk442snbHmnfOWIehpfYLUcEi+UajAgMBAAEC\n" +
                    "gYAy06E0LmtRPreCVoVwp846LDL7k9CoitRP0ErtXwf46kxvI320h9PCybmwMq/D3SSavnJB4Pj6\n" +
                    "9T5m4GKvq6AYqLpR2FUjJXuEGs/6LbJhNFraHtcsnlzPXpL158WmLGSk5I7D8CBAg92Bv9Ik/mPb\n" +
                    "dKwD4Qlqnu0VDT8AOyfW8QJBANek8epnEKrmc2rMmLjtjXxrV1BJKPeZ1Stj7KNuKkvUQAVA8GTz\n" +
                    "kwNqw5gW7U8cbccz72ius5U63alH8fJ7zhkCQQDNWTmBLBcjGJVrU0qOj+zzzTD9kd+joRhkN11U\n" +
                    "tGcn7dSsbG/FGoYco/3KDd3SxRWOpuT2LVX3BOfUUzLMXBobAkANraO+p4zyGi4F/zZMvJy14KUo\n" +
                    "LYvQsMxJtUvkTe+W4b6x1p2o+Z5AoDwJ4KJq2zXHZe7wp+wyMgqvWyXoqJu5AkA5I6a4psgZX/HT\n" +
                    "121blfjdLi9/n2OXaHAdErrJoxlBJxCSmenP8r12orsvygP7bhO9ifsT9TGCdr77edsculQZAkBd\n" +
                    "LdlDCpeHtC4LHCHnbGyGjGl7TUH93WojVML72TWgSjNWSK7TUOsXTn4DorgQY02gMzzGvHYu+R6B\n" +
                    "zy4upbxi";

            String content = "123456";
            //用公钥加密
            byte[] publicEncrypt = publicEncrypt(content.getBytes(), getPublicKey(publicKey));
            //加密后的内容Base64编码
            String byte2Base64 = Base64.getEncoder().encodeToString(publicEncrypt);
            System.out.println("公钥加密并Base64编码的结果：" + byte2Base64);

            //加密后的内容Base64解码
            byte[] base642Byte = Base64.getDecoder().decode(byte2Base64);
            //用私钥解密
            byte[] privateDecrypt = privateDecrypt(base642Byte, getPrivateKey(privateKey));
            //解密后的明文
            System.out.println("rsa解密后的明文: " + new String(privateDecrypt));

            // 解密前端
            String password = "kzzdBmpn6/ifWH/FoTzK6rfMS98+8fzkOOaIXhsYsKeCK3JRWnW7fLs4LauixcsDYZR0Uz2SRZIuHr/FjKsP4bZd9a4vemt/lzO5jRhkxaC1D1ptn8UWG3tFxxKiHZv2I5RXxg2hcC0MF2+xYFVXRO4qzYLBPbmulRo2zDBY/TRy3p24h4KNlcFyJyREEFPIjWIozfDlazU=";
            password= password.replaceAll(" ","+");

            //加密后的内容Base64解码
            byte[] passwordByte = base642Byte(password);
            //用私钥解密
            byte[] passwordDecrypt = privateDecrypt(passwordByte, getPrivateKey(privateKey));
            //解密后的明文
            System.out.println("rsa解密后的密码: " + new String(passwordDecrypt));

            System.out.println("————————————————————————");

            //des加密和解密
            String desKey = "JSDGADSG";
            String userId = "WangHaiMing";
           userId = desEncrypt(userId, desKey);
            System.out.println("des加密后: " + userId);

            String decrptUserId = desDecrypt(userId, desKey);
            System.out.println("des解密后: " + decrptUserId);*/

            String desKey = "JSDGADSG";
            String message = "5bea90520afef1e2bebdfe85039a6e40f902963c7fc3c30938ed1726e8d2045297eb4b0df3b67f422f8f7201a138f0191df5d569d99db6b6422e7a47c8ecca3e353dfe02dd13729590679fd4225f112276a10e72608591ee16d42a05fffe55267bc5d134d94e359b396d7242fc0e1e1aca83c62e4b8ce5a8d3e569f4d3df0f31eafc92982a0695a29909aab9ab71f4797c644a4787de5d86cab230377dc08b8c";
            String aa = "{\n" +
                    "    \"performanceAppraisaId\": \"1381919165880995841\",\n" +
                    "    \"jobCategory\": \"INSPECTIONPERSON\",\n" +
                    "    \"executionTime\": \"02:00:00\",\n" +
                    "    \"performancePlanType\": \"0\"\n" +
                    "}";
            System.out.println("des加密后: " + desEncrypt(aa, desKey));
            System.out.println("des解密后: " + desDecrypt(message, desKey));


//            String decrptUserId = desDecrypt(userId, desKey);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
