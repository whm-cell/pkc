package com.fx.dense.utils;

import com.fx.dense.exception.BusinessException;

import javax.validation.constraints.NotBlank;
import java.io.UnsupportedEncodingException;

/**
 * @program: pkc
 * @description: 数组相关
 * @author: WangHaiMing
 * @create: 2021-10-12 14:06
 **/
public class ArrayUtil {


    /**
     * 合并数组   System.arraycopy 合并方式效率较高
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 字节数组 补齐16位
     * @param context   值
     * @param characterSet  字符集   等于 16位时  直接使用 字符集
     * @param defaultByteLength 需要补全的长度  一般使用  8 或者 16 个字节
     * @return
     */
    public static byte[] byte16Completion(String context,String characterSet,int defaultByteLength){
        try{
            byte[] raw = new byte[defaultByteLength];

            if (context.length() >= defaultByteLength) {
                raw = context.getBytes(characterSet);
            }
            raw = buildCom(context, characterSet, defaultByteLength, raw);
            return raw;
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * iv偏移量自动补齐
     * @param iv
     * @param characterSet
     * @param defaultByteLength 需要补全的长度  一般使用  8 或者 16 个字节
     * @return
     */
    public static byte[] byte16CompletionIv( String iv, String characterSet,int defaultByteLength){
        try{

            if(iv.length() > defaultByteLength){
                throw new BusinessException("偏移量必须是"+defaultByteLength+"位，少于会自动补全(不建议)");
            }

            byte[] raw = new byte[defaultByteLength];

            if (iv.length() == defaultByteLength) {
                raw = iv.getBytes(characterSet);
            }
            raw = buildCom(iv, characterSet, defaultByteLength, raw);
            return raw;
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 补全细节
     * @param iv
     * @param characterSet
     * @param defaultByteLength
     * @param raw
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] buildCom(@NotBlank(message = "iv偏移量不可为空") String iv, String characterSet, int defaultByteLength, byte[] raw) throws UnsupportedEncodingException {
        if (iv.length() < defaultByteLength && iv.length() > 0) {
            byte[] bytes = iv.getBytes(characterSet);

            int length = bytes.length;
            byte[] data2 = new byte[16 - length];
            raw = byteMerger(bytes, data2);
        }
        return raw;
    }

}
