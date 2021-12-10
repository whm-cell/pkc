package com.fx.dense.model.rsa;

import lombok.Builder;
import lombok.Data;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-10 13:15
 **/
@Data
@Builder
public class PublicKeyEncryptionDto {

    /**
     * 待加密的文本
     */
    private String text;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 填充方式
     */
    private String filling;

    /**
     * 是否支持超长文本
     */
    private String veryLongText;

    /**
     * 超长分割符
     */
    private String extraLongSplitCharacter;

    /**
     * 输出格式
     */
    private String output;

    /**
     * 字符集
     */
    private String characterSet;

    /**
     * 私钥密码
     */
    private String secretKey;


}
