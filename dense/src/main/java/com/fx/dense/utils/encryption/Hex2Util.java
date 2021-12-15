package com.fx.dense.utils.encryption;

import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @program: pkc
 * @description: 二进制转换工具类
 * @author: WangHaiMing
 * @create: 2021-10-11 14:56
 **/
public class Hex2Util {


    /**
     * 十六进制字面量
     */
    private static final String HEXADECIMAL_LITERAL ="0123456789ABCDEF";


    /**
     * 二进位组转十六进制字符串
     * @param buf 二进位组
     * @return 十六进制字符串
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转二进位组
     * @param hexStr 十六进制字符串
     * @return 二进位组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];

        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * String转16进制片段* (org.bouncycastle.util.encoders.Hex)
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encodeHex(String data) throws UnsupportedEncodingException {
        byte[] encode = Hex.encode(data.getBytes());
        return new String(encode);
    }

    /**
     * Hex转String片段* (org.bouncycastle.util.encoders.Hex)
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decodeHex(String data) throws UnsupportedEncodingException {
        byte[] decode = Hex.decode(data.getBytes());
        return new String(decode);
    }


    /**
     * Base64返回值应该为byte[]数组类型
     * @param data
     * @return
     */
    public static byte[] encodeBase64Byte(String data){
        byte[] encode = Base64.getEncoder().encode(data.getBytes());
        return encode;
    }
}
