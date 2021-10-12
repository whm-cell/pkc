package com.fx.dense.utils;

import com.fx.dense.base.Const;
import com.fx.dense.exception.BusinessException;
import com.fx.dense.model.AesRequestModel;
import com.fx.dense.model.DesRequestModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @program: pkc
 * @description: aes 加密类
 * @author: WangHaiMing
 * @create: 2021-10-08 16:00
 **/
@Component
public class AesUtil {

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

    private static final String AES_SHA1PRNG = "SHA1PRNG";



    /**
     * des加密
     * @param model
     * @return
     */
    public static String aes(AesRequestModel model) {
        try{
            KeyGenerator generator = KeyGenerator.getInstance(model.getType());
            SecureRandom random = SecureRandom.getInstance(AES_SHA1PRNG);
            random.setSeed(model.getKey().getBytes());
            generator.init(Integer.parseInt(model.getDataBlock()), random);
            SecretKey secretKey = generator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, model.getType());
            Cipher cipher = Cipher.getInstance(model.getBuildMode());

            //  这种模式不可用（iv 自定义参数）
            if(Const.Ecb.equals(model.getEncryptionMode())){
                if(model.getIsItEncrypted() == Cipher.ENCRYPT_MODE){
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    return getEnResult(model.getContext(), model, cipher);
                }else {
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    return getDeResult(model.getContext(), cipher);
                }
            }else{
                IvParameterSpec iv = new IvParameterSpec(ArrayUtil.byte16CompletionIv(model.getIvOffset(),model.getCharacterSet(),Const.LENGTH_OF_THE_COMPLETION_BYTE[1]));
                if(model.getIsItEncrypted() == Cipher.ENCRYPT_MODE){
                    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                    return getEnResult(model.getContext(), model, cipher);
                }else {
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                    return getDeResult(model.getContext(), cipher);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
            throw new BusinessException(e.getMessage());
        }
    }



    /**
     * 获取 根据 加密方式获取二进制转换十六进制后的结果
     * @param content
     * @param cipher
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String getDeResult(String content, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] byteContent = Hex2Util.parseHexStr2Byte(content);
        return new String(cipher.doFinal(byteContent));
    }

    /**
     * 获取根据 解密方式获取十六进制转换二进制后，再次转为string的结果
     * @param content
     * @param model
     * @param cipher
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private static String getEnResult(String content, AesRequestModel model, Cipher cipher) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytesContext = ArrayUtil.byte16Completion(content, model.getCharacterSet(),Const.LENGTH_OF_THE_COMPLETION_BYTE[1]);

        if (model.getOutput().equals(Const.Base64)) {
            BASE64Encoder encoder = new BASE64Encoder();

            String encode = encoder.encode(bytesContext);
            return encode;
        }

        return Hex2Util.parseByte2HexStr(cipher.doFinal(bytesContext));
    }
}
