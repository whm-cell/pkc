package com.fx.dense.utils.rsa;

import cn.hutool.core.util.HexUtil;
import com.fx.dense.base.Const;
import com.fx.dense.exception.BusinessException;
import com.fx.dense.model.rsa.GenerateKeyPairDto;
import com.fx.dense.model.rsa.PublicKeyEncryptionDto;
import com.fx.dense.utils.Hex2Util;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.assertj.core.util.Lists;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-04 16:25
 *
 *
 *
 *
 *
 *
 * 1.RSA_PKCS1_PADDING 填充模式，最常用的模式
 *
 * 要求:
 * 输入：必须 比 RSA 钥模长(modulus) 短至少11个字节, 也就是　RSA_size(rsa) – 11
 * 如果输入的明文过长，必须切割，　然后填充
 *
 * 输出：和modulus一样长
 *
 * 根据这个要求，对于512bit的密钥，　block length = 512/8 – 11 = 53 字节
 *
 * 2.RSA_PKCS1_OAEP_PADDING
 * 输入：RSA_size(rsa) – 41
 *
 * 输出：和modulus一样长
 *
 * 3.for RSA_NO_PADDING　　不填充
 *
 * 输入：可以和RSA钥模长一样长，如果输入的明文过长，必须切割，　然后填充
 *
 * 输出：和modulus一样长
 *
 * 跟DES，AES一样，　RSA也是一个块加密算法（ block cipher algorithm），总是在一个固定长度的块上进行操作。
 *
 * 但跟AES等不同的是，　block length是跟key length有关的。
 *
 * 每次RSA加密的明文的长度是受RSA填充模式限制的，但是RSA每次加密的块长度就是key length。
 **/
public class RsaUtils {


    public static String RSA_ALGORITHM = "RSA";
    public static String UTF8 = "UTF-8";

    /**
     * encryption algorithm RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * RSA Maximum Encrypted Plaintext Size
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA Maximum decrypted ciphertext size
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    static {
        configureSecProvider();
    }

    /**
     * Configures security providers which are used by the library. Can be called
     * multiple times (subsequent calls won't have any effect).
     * <p>
     * This method must be called before any other usage of the code from canl API.
     */
    public static void configureSecProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * RSA公钥
     *
     * @param usePKCS1  是否采用PKCS1填充模式
     * @param publicKey 公钥
     * @return 公钥
     * @throws Exception 加密过程中的异常信息
     */
    public static RSAPublicKey generatePublicKey(boolean usePKCS1, byte[] publicKey) throws Exception {
        KeySpec keySpec;
        if (usePKCS1) {
            // PKCS1填充
            DLSequence sequence = (DLSequence) ASN1Primitive.fromByteArray(publicKey);
            BigInteger v1 = ((ASN1Integer) sequence.getObjectAt(0)).getValue();
            BigInteger v2 = ((ASN1Integer) sequence.getObjectAt(1)).getValue();
            keySpec = new RSAPublicKeySpec(v1, v2);
        } else {
            // PKCS8填充
            keySpec = new X509EncodedKeySpec(publicKey);
        }
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(keySpec);
        return pubKey;
    }
   /* public static String getPaddingString(PaddingMode paddingMode) {
        switch (paddingMode) {
            case NO_PADDING:
                return "NoPadding";
            case PKCS7_PADDING:
                return "PKCS7Padding";
            case PKCS1_ENCRYPT_PADDING:
                return "PKCS1Padding";
            case OAEP_PADDING:
                return "OAEPPadding";
            case PSS_PADDING:
                return "PSS";
            default:
                return null;

        }
    }*/

    public static byte[] publicKeyEncryption(PublicKeyEncryptionDto dto) {

        try {
            byte[] contextBytes =dto.getText().getBytes(dto.getCharacterSet());

            int inputLen = contextBytes.length;


            String publicKey = dto.getPublicKey();

            boolean type = dto.isType();

            //1. 如果为hex 这样转为base64
            if (publicKey.matches(Const._HEXADECIMAL_REGULAR_16)) {
                publicKey = Hex2Util.decodeHex(publicKey);
            }

            //2. 替换掉头和尾
            publicKey = publicKey.replace("-----BEGIN -----", "");
            publicKey = publicKey.replace("-----END -----", "");

            java.security.Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );

            BASE64Decoder decoder = new BASE64Decoder();

            byte[] bytes = decoder.decodeBuffer(publicKey);

            RSAPublicKey rsaPublicKey = generatePublicKey(type, bytes);
            Cipher cipher = Cipher.getInstance(dto.getFilling());
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 字符分段
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(contextBytes, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(contextBytes, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return null;
    }






    public static void main(String[] args) {
        List<String> my = null;
        //1.1完成测试  pkcs8  base64 输出
        // my = pkcs8containSecretToBase64();
        // 1.2 完成测试 pkcs8 公钥为hex输出  私钥为base64输出
        // List<String> my = pkcs8containSecretToPublicKeyHex();

        // 1.3 完成测试 pkcs8 公钥为base64输出  私钥为hex输出
        // List<String> my = pkcs8containSecretToPrivateKeyHex();

        // 1.4  完成测试 pkcs8 公钥为hex输出  私钥为hex输出
        // my = pkcs8containSecretToPrivateKeyHexAndPublicKeyHex();

        // 1.5 完成测试 pkcs1 下  均为 base64 十六位输出的密钥
        //  my = pkcs1containSecret();

        // 1.6 完成测试 pkcs1 下  均为 hex 十六位输出的密钥
        // my = pkcs1containSecretToPrivateKeyAndPublicKeyHex();

        // 1.7 完成测试 pkcs1 下  公钥为base64输出  私钥为hex输出
        // my = pkcs1containSecretToPrivateKeyHex();

        // 1.7 完成测试 pkcs1 下  公钥为hex输出  私钥为hex输出
        // my = pkcs1containSecretToPublicKeyHex();

         my = pkcs1containSecretToBase64();



        PublicKeyEncryptionDto build = PublicKeyEncryptionDto.builder()
                .publicKey(my.get(0))
                .privateKey(my.get(1))
                .secretKey("")
                .output(Const.RSA_OUTPUT_METHOD[2])
                .text("七龙珠")
                .characterSet("utf-8")
                //.filling("RSA/ECB/PKCS1Padding")
                //.filling("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
                //.filling("RSA/None/PKCS1Padding")
                .filling("RSA/ECB/NoPadding")
                .build();

        try {
            // 公钥加密
            buildParametersAndResultSetsPublicKey(build);

            // 私钥解密
             privateKeyDecryption(build);

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public static String buildParametersAndResultSetsPublicKey(PublicKeyEncryptionDto build) {
        byte[] bytes = publicKeyEncryption(build);
        if (build.getOutput().equals(Const.Base64)) {
//            BASE64Encoder encoder = new BASE64Encoder();
//
//            String encode = encoder.encode(bytes);
            return java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return Hex2Util.parseByte2HexStr(bytes);

    }

    public static String privateKeyDecryption(PublicKeyEncryptionDto dto) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        String privateKey = dto.getPrivateKey();

        String text = dto.getText();

        //1. 如果为hex 这样转为base64
        if (privateKey.matches(Const._HEXADECIMAL_REGULAR_16)) {
            privateKey = Hex2Util.decodeHex(privateKey);
        }
        byte[] decode = null;
        // 1.文本需要通过base64解密 获得 字节数组  进行解密
        if (text.matches(Const._HEXADECIMAL_REGULAR_16)) {
            decode = Hex2Util.parseHexStr2Byte(text);

        }else {
            decode = java.util.Base64.getDecoder().decode(text);
        }

        // 格式化私钥
        byte[] encoded = formatThePrivateKey(dto, privateKey);

        // 开始解密
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        // Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(dto.getFilling());

        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = decode.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(decode, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(decode, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    private static byte[] formatThePrivateKey(PublicKeyEncryptionDto dto, String privateKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        String secretKey = dto.getSecretKey();

        PrivateKey encryptedPrivateKey;
        byte[] encoded;
        if(StringUtils.isNotBlank(dto.getSecretKey())){
            if(privateKey.contains("Proc-Type: 4,ENCRYPTED")){
                throw new BusinessException("暂不支持pkcs1下 （已加密私钥）密码的 解密");
            }
            // 需要先执行解密操作
            privateKey = privateKey.replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "");
            privateKey = privateKey.replace("-----END ENCRYPTED PRIVATE KEY-----", "");
            EncryptedPrivateKeyInfo pkInfo = new EncryptedPrivateKeyInfo(Base64.decode(privateKey));
            PBEKeySpec keySpec = new PBEKeySpec(secretKey.toCharArray());
            SecretKeyFactory pbeKeyFactory = SecretKeyFactory.getInstance(pkInfo.getAlgName());
            PKCS8EncodedKeySpec encodedKeySpec = pkInfo.getKeySpec(pbeKeyFactory.generateSecret(keySpec));
            KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
            encryptedPrivateKey = keyFactory1.generatePrivate(encodedKeySpec);
            // 私钥先通过  ‘私钥解密’ 后才可以进行文本解密
            // 得到私钥解密后的私钥字节数组
            encoded = encryptedPrivateKey.getEncoded();
        }else {
            BASE64Decoder decoder = new BASE64Decoder();
            if(privateKey.contains(Const.PKCS1_PRIVATE_KEY_HEADER)){
                // 该功能暂未实现
                // 需要先执行解密操作
                privateKey = privateKey.replace("-----BEGIN RSA PRIVATE KEY-----", "");
                privateKey = privateKey.replace("-----END RSA PRIVATE KEY-----", "");
            }else {
                // 需要先执行解密操作
                privateKey = privateKey.replace("-----BEGIN -----", "");
                privateKey = privateKey.replace("-----END -----", "");
            }
            encoded =  decoder.decodeBuffer(privateKey);
        }
        return encoded;
    }
























































    /**
     * Pkcs8转Pkcs1
     *
     * @param isPrivateKey
     *            是否是私钥转换
     * @param buffer
     *            Pkcs1秘钥
     * @return Pkcs8秘钥
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static byte[] pkcs8ToPkcs1(boolean isPrivateKey, byte[] buffer) throws Exception {
        if (isPrivateKey) {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(buffer);
            return privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
        } else {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(buffer);
            return subjectPublicKeyInfo.parsePublicKey().toASN1Primitive().getEncoded();
        }
    }


    /**
     * Pkcs1转Pkcs8
     *
     * @param isPrivateKey
     *            是否是私钥转换
     * @param buffer
     *            Pkcs1秘钥
     * @return Pkcs8秘钥
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static byte[] pkcs1ToPkcs8(boolean isPrivateKey, byte[] buffer) throws Exception {
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
        ASN1Primitive asn1Primitive = ASN1Primitive.fromByteArray(buffer);
        if (isPrivateKey) {
            PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, asn1Primitive);
            return privateKeyInfo.getEncoded();
        } else {
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, asn1Primitive);
            return subjectPublicKeyInfo.getEncoded();
        }
    }

    public static List<String> pkcs8containSecretToPublicKeyHex() {

        String publicKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d4947664d413047435371475349623344514542415155414134474e4144434269514b42675143307659506a657a52736337544e797a454c663752456e4a7a4d0d0a30597250416839313248564d685932544973635a53706a65732f4f664468396a49723671547547344a4952647a3631484a3278485875477430754773593230520d0a5459457278484c466644544576453574724f747953466e762f344a757859754a41696a55644c6649354a7877386d59715343595567445246657635766a344b380d0a436a453852713663492f4d5847422b4b68514944415141420d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        String privateKey = "-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
                "MIICrjAoBgoqhkiG9w0BDAEFMBoEFHTNpbY7ZVueWUDdZpA9mT6ng803AgIIAASC\n" +
                "AoBlp6mdq+KtZMTGA/sSceDpHETR7map7fyLlqhguKlNOD5i/Dz8t2rnRj0XC+xO\n" +
                "SuEOrjfC90b4pY0wlhU6iNqxwR2S6ClgQ2QyxlfihMb43Cus42nTkxlYot8T1P1M\n" +
                "fA11pfj36wGVRIBpEyeUCuEFuShSTFz5lP4nwPd2WNFo8TGKM4vAbscw7U6megZp\n" +
                "38iQwrR5O3B/qq+b4G8sokvarTWTPdah0SR+TNuXBS2I2aYd7RCcziUium4WmRPv\n" +
                "KG4Mj4IKiEbhPRXmB3VFHSFhkF2QqhkQonJIBu87hmUHIipJUqmxbfgDYzFCzgni\n" +
                "BgsgCjFuYMfC4c60all6+O/TBc5GtpivcO44s1iGgWtcKlBXuyEcPVjnKFEzBERl\n" +
                "aDwagfeAi+ZBpgbHfzqWsfPKTnjGPU8ACuqbCcqiUuVwzjyuXG8UeKACqBIZ13p+\n" +
                "A52ILvNlXhAOK+MohH9cxJGMzuinKFx+8XkGEMdRD0Qp0VeKzm5dc2Eu2ZrNaxYY\n" +
                "H/48WcCesNZRgXq8J3CTqXpnl+eQUik3TQ+7PUf5ryVOluOSl9C5nT72n3PfGZkY\n" +
                "HF4wqyL33r0HSEFpdUXdsbei9q7WHBp1EfhIS83qOBoW/THmpqojQ3J9kzLwKC3K\n" +
                "eg0XtaO7bNwv9IYmRrTgs7z1oP7pkkzJsafmryeF6SATdUhjRk2/MogMyftAWDd3\n" +
                "2eymBDCXkAzZO6AIMAox7HWU2FMRWRCriXaWYAydUaSEAxm04RbEgGLZYxbfxZYl\n" +
                "44LW1udGc06198Hk1sTEMGqHZPMRnWeh0uQw9BjQHiHxLdKEamMIhLkaJjipyLFL\n" +
                "0f5nMDERaH8XxiYcgJj1DBat\n" +
                "-----END ENCRYPTED PRIVATE KEY-----";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }


    /**
     * pkcs1 转 pkcs8   公钥
     * @param rawKey
     * @return
     * @throws Exception
     */
    public static byte[] formatPkcs1ToPkcs8(String rawKey) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] bytes = decoder.decodeBuffer(rawKey);
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag);
        ASN1Object asn1Object = ASN1ObjectIdentifier.fromByteArray(bytes);
        SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier,asn1Object);

       return publicKeyInfo.getEncoded();

        // return encoder.encode(encoded);
//        PrivateKeyInfo privKeyInfo = new PrivateKeyInfo(algorithmIdentifier, asn1Object);
//        byte[] pkcs8Bytes = privKeyInfo.getEncoded();
//        return encoder.encode(pkcs8Bytes);
    }


    /**
     * 完成测试  pkcs8  base64 输出
     *
     * @return
     */
    public static List<String> pkcs1containSecretToBase64() {

        List<String> list = Lists.newArrayList();
        String publicKey = "-----BEGIN -----\n" +
                "MIGJAoGBAISKY9FF5UEmIX7Yap4UobqUT6hw8dAK/IkX3vQI5SXW398OJjzf0kIA\n" +
                "NNLqtWPsmE4JJSIo4eUA7aQSwxGpkvxdMonKz0wi5BdHW+KB9tCGu4Ji7FlmYUqS\n" +
                "u0c05wsCFxxwXbHwEjLflGqf19/XC/0/U8et61yPaoV4hSslOCZzAgMBAAE=\n" +
                "-----END -----";
        String privateKey = "-----BEGIN -----\n" +
                "MIICXAIBAAKBgQCEimPRReVBJiF+2GqeFKG6lE+ocPHQCvyJF970COUl1t/fDiY8\n" +
                "39JCADTS6rVj7JhOCSUiKOHlAO2kEsMRqZL8XTKJys9MIuQXR1vigfbQhruCYuxZ\n" +
                "ZmFKkrtHNOcLAhcccF2x8BIy35Rqn9ff1wv9P1PHretcj2qFeIUrJTgmcwIDAQAB\n" +
                "AoGABxQM+KQu9ie/KjMMnpyjdn9tMaW2hFHR4tBvi8Dd3AN5uUPcLuwVIok4SPtX\n" +
                "7Jo6GBDM9uIQDlbaWiTWXh8cRmIA2AK62LaFtqcYC6XTdGN4gfqInKyYPRcV9kuK\n" +
                "/2Zk3PzH6OZtc7T9u4pZwhwrnCNxaQlgszq+c48Ti+Y6ZcECQQDdkyccS+eb590R\n" +
                "HctbJoVvDf1XkD3zbeNmFBup4SHZVsM+aVh3Qk3m72Mxf4ZNFXchWzfsEOYuNOUd\n" +
                "TLgz+k3JAkEAmSIEgT+OjrUL7THk/CnzZlBgQCAwIiJiQAgz6uRXpj7I1lBi9LC5\n" +
                "yNmnK0BTQPylMuw9DFUBZE4IsU1L5oqAWwJAYFermdInUtLufJLH6UpGmpMqpPhC\n" +
                "96XASpJTtPn/DdF785c6sCBUXyWXq94XN00uD4LqPerbKxw0wxiov3EGgQJAa+KH\n" +
                "gfehG/F5gZbRWMbFcKPX69XBk4rd5Xbh/O76wWkiXbssvG91tIeDOvmKrhJQ4NuG\n" +
                "9xU8ccp4XVGz06xoWQJBAMTc1iK0sbRoYa05c6AE6QEQ8ndUJDuzYHH3US7z7vXw\n" +
                "5O1hYhLsFn1I3HXQqrJS+3BdmtSwoALWsU+xYtUzwVs=\n" +
                "-----END -----";
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }



    /**
     * 完成测试  pkcs8  base64 输出
     *
     * @return
     */
    public static List<String> pkcs8containSecretToBase64() {

        List<String> list = Lists.newArrayList();
        String publicKey = "-----BEGIN -----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgYBeifg0T6haw6wgswTIVwHPE\n" +
                "FAgRndOYsq+0hvWKhDLKuvcolipW4SNvO/H7NnGtRHcrDjKSTCDm4l28iC7650mJ\n" +
                "wwBBF1BEnIAvcstrbWFi9c5OW8ft6GIDcjiA6wSueBfCakaLTD9uvcHU/yBHWtwi\n" +
                "lno1iG1Q2svnQ7c1oQIDAQAB\n" +
                "-----END -----";
        String privateKey = "-----BEGIN -----\n" +
                "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAOBgF6J+DRPqFrDr\n" +
                "CCzBMhXAc8QUCBGd05iyr7SG9YqEMsq69yiWKlbhI2878fs2ca1EdysOMpJMIObi\n" +
                "XbyILvrnSYnDAEEXUEScgC9yy2ttYWL1zk5bx+3oYgNyOIDrBK54F8JqRotMP269\n" +
                "wdT/IEda3CKWejWIbVDay+dDtzWhAgMBAAECgYEAyFPjJjgm0v3wznq57MN9RUBz\n" +
                "28AKyDfCoRYAnEzW05pegvTVEU6pdLJEYd8CmMkuS4XSZAWkD0ybH6lCCb8JOVwb\n" +
                "iL0GixlqBD87k6hLUegt31U1czi2vpUbvw+4+gHkBL7WW4W0O0caHkHYcQ2GlTjV\n" +
                "b5o7QDKrBlEB3DnozIUCQQD+03HTea4TMirvvmuMxQN3D/tOkQ34cMeZoDGq2GD2\n" +
                "SgVWzPbCVts87qjVHyZ2ErDfMkqTwCN46K5z7n1wUHBPAkEA4Wi7jZREjaTS+OFF\n" +
                "UZ4ToUjajTwRf9pjWqAT9XZr52KOe2I6GuN9c54DinZ9R9pE4H9vH6EeFFENVDsH\n" +
                "2dQPDwJBAMGc4gUtlIddPKY/ZfPk1Mcny2dcauLyvZUJ0/LcyH8YqonS3UG3QIhB\n" +
                "ROH48v4PIXfT/DnaaJk7ISFQeNpXI50CQQCqssOFngikM3PJId96xXkcJK3NUvX1\n" +
                "mo6u7JbwxjNE0LAR4/sYrbotOjZ4Pu704OhUEm02yK6cuByd8u4pGZwlAkEA2uG8\n" +
                "qoVJAy8w9gJkTMcgekaG1RWpnPCZ52yFOGoPykKV4hLvEdTUFEvce3ezMe4wNYuO\n" +
                "4kIil3btJKxZ+UWrwA==\n" +
                "-----END -----";
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }


    public static List<String> pkcs8containSecretToPrivateKeyHex() {

        String publicKey = "-----BEGIN -----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCHJ5HhdtxnpPsRL6fIszDdz/CB\n" +
                "g+iG9TI4V1sQF3SdA1CXsVOZRnXP7mWcIwOI2OQ/heQg8OMwvSXHoY1NwQXEHr/h\n" +
                "ucJY/lwDki2+ZqHp6MtRaoGVzX6gO74wKb2ytXd5hzjOzTivYbIYPV5dDVYKI1BG\n" +
                "gGumx4uKn3i/NI63AwIDAQAB\n" +
                "-----END -----";
        String privateKey = "2d2d2d2d2d424547494e20454e435259505445442050524956415445204b45592d2d2d2d2d0d0a4d494943726a416f42676f71686b694739773042444145464d426f45464c6d626d747852637a41596f474a755964446638533176336a677541674949414153430d0a416f43546739353477682f4a794a6a4f5a535239757433516d31734f386a667362784a6a7447716e33446f5834697965414138434862306a4439485a45667a300d0a513273335a36376f53376566334b41486941565362454a496f7a5a5661477134566b586d66756c5a4c48384e412f4f66634166704b754734725149436e6c4d730d0a384a794344426474324a3958674a354d716264476b6364637061376d35566a78452b6f464562556947415937784a6a6350797367424a56514a483832444a46510d0a623053304a526c32614a47414e69567366726b6c3034366264582b586876615a6a4b556f6e344964723859502b7370734350462f53646f4d4e572f315875764b0d0a7636756d414e70644b4e6d4f6b7a524b2f716d4843355a763775593532544c58563030514b2b674f4b6a456f5a753247784a71593939394f33695372384967520d0a33324e52616e63666c6f7574702f417131716f4b685971444853556e6f6e524330374e4b3371397455686a4e79624951735432564565763865302f5743627a780d0a534f6f4f314e2b35393759326170724d38467a5a675a544477794636374b4f39344b343673303470413670782b5376624c75714f4563385734473151624343310d0a4652576e42614869302f703573637932384c376177696c6568564c686846744a6635534549505a36795550376f5175596c716d614a4b66716a4d35664f3132520d0a3645624d5539426763656369502b6f573473705773516c324f6b2f71524957392b79702f38684d70692f587a76416e54327133716d62424670665a6d474a356f0d0a57596a364b4376666a6253376456554630416155306c55682b6c2b58465147786d376e42424f62634155586135776b4a6b316b714d74556479317859444c51430d0a3763336373653275797551666d5748466552767375694a2b4168537039364539555438334431346b546277596f6d42704267356352694976674a774a5a4a466a0d0a564974375432694859373066396a4b6545364a45417a674830594b7079786b727a4e47487850314c2f4770613463335353685350524568454a484b6a594547540d0a4876675573677a74553659586632384233486735636f446573715939594650666c3330586535624438386d66584f747a417662676930467239693778684c45390d0a51754568316644465941612b5874546c41706870516d574c0d0a2d2d2d2d2d454e4420454e435259505445442050524956415445204b45592d2d2d2d2d0d0a";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }

    public static List<String> pkcs8containSecretToPrivateKeyHexAndPublicKeyHex() {

        String publicKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d4947664d413047435371475349623344514542415155414134474e4144434269514b42675143704b58454a6447434735762b2b333359433854454b594376780d0a37616d546d37496d33723953524c4230446f797a68506d3241764b525a5a36556d5062673772356a7372547465666458544457485747584e6234347a757a686c0d0a51457a724174592f4e7a52696c356733743131515066552f507a6b3833332b6e43394372486250787843586d365041763359644254364f2f344b7376466a7a490d0a4c6548705068653035473732586c6c4874514944415141420d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        String privateKey = "2d2d2d2d2d424547494e20454e435259505445442050524956415445204b45592d2d2d2d2d0d0a4d494943726a416f42676f71686b694739773042444145464d426f45464d70514e6a6b39624e504c78714b724542783968544e4e7869317041674949414153430d0a416f425a4c74735776696534582b424d432b2f5666685934673173504161614b3944795a78415541666f616e497347506955706437574b784955614c573531390d0a493370704b7435673243495944434b3657486b524b6969784b71495037486b3046727536356e2f4e724747756149412b354c736c324c696f7533444d746561620d0a443031635a536e724e2f45736342313850755539445645336d5866514352305058727351357a37633370394b37545867776a306147615854444c49786447462f0d0a4f726a7a2b514e692f334b555a626e5541516b34597056416d3433667541666e4e6f656e58474a4364694e71444f49354c486b77487336625a5549356a614c570d0a336e643943644e56305569665a5655712f493735644e59536372367172516776345858536c714a577835333434706d62434a6b744c437266716b47794b5053370d0a34457641313177586270394e78616a53426153783069485958514b495779364e52734c5a4d6d662b5539396b424377687577344974564e57587731544e3456440d0a7a6a4f392b5071714a31456e682f61323337614139355759314e786562707765576a4731486a6a72614b6542595a6a6b6f33654e69763777594e55794b5447620d0a4a762f2b497a5154516d6a552f3538697372775569457757666f63725957573535466672704c3833524a396c307630684d783267676b3773626a546f524b6d790d0a674e4e6f39575456767346474747505047473045334f4472482f334c34456f356f69546c6a42615a3962564241446a4a7941544d453262457641436b554a565a0d0a477075664e674d717a35396354424e63724d76516962384d4f34595637707030442b4f37484537745872694c6e31344c634b69504b4f415038783559596365630d0a486a412b662b54492b41425347516f414d6d6b524739454e587a5951646554546d4a4b744967437a76756b56616c764b47762f77494d497343344f41444c42380d0a43726341316f7977414a74502f645044557a5a4f46747245617747562f344d4a506b4a4f54722b5962385332594a6a597544636155754a424f6e482f586b66760d0a426d55712f6361327171773835446c6a76506c50774f777765436f30445a76735a6f6f6532324f7a58526c544d503037536e47344f597338334a6865713459730d0a786734446550693434543068563450757872346b585978480d0a2d2d2d2d2d454e4420454e435259505445442050524956415445204b45592d2d2d2d2d0d0a";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }
}
