package com.fx.dense.utils.encryption;

import com.fx.dense.base.Const;
import com.fx.dense.exception.BusinessException;
import com.fx.dense.model.DesRequestModel;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.*;

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
