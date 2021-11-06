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

    private Integer secretKeyBits;

    private String secretKeyFormat;

    private String outputFormat;

    private String secretKey;

}
