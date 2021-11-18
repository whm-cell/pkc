package com.fx.dense.model.rsa;

import lombok.Builder;
import lombok.Data;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-06 14:40
 **/
@Builder
@Data
public class GenerateKeyPairDto {

    private String headType;

    /**
     * 位数
     */
    private Integer secretKeyBits;

    /**
     * 编码格式
     */
    private String secretKeyFormat;

    /**
     * 输出格式
     */
    private String outputFormat;

    /**
     * 秘钥密码
     */
    private String secretKey;

}
