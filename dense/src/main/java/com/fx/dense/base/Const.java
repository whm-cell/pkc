package com.fx.dense.base;

/**
 * @program: pkc
 * @description: 常量
 * @author: WangHaiMing
 * @create: 2021-10-12 13:40
 **/
public class Const {


    /**
     * 加密输出格式  base64
     */
    public static final String Base64 = "base64";

    /**
     * 加密输出格式  hex
     */
    public static final String Hex="hex";

    /**
     * 加密模式    ECB 模式无法使用iv偏移量   所以加到常量里备用
     */
    public static final String Ecb = "ECB";

    /**
     * 加密还是解密  1 加密 2 解密
     */
    public static final Integer[] ENCRYPT_OR_DECRYPT = {1,2};


    /**
     * 补全字节的长度  一般des 是 8 位  aes 是 16位
     */
    public static final Integer[] LENGTH_OF_THE_COMPLETION_BYTE  = {8,16};


}
