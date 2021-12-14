package com.fx.dense.utils.rsa;




import com.alibaba.fastjson.JSONObject;
import com.fx.dense.base.Const;
import com.fx.dense.model.rsa.GenerateKeyPairDto;
import com.fx.dense.utils.Hex2Util;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.*;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-06 14:38
 *
 * 逻辑来自：http://defned.com/post/java-rsa-pkcs1/
 *
 * 1. 使用 RipeMD160 算法
 *      Security.addProvider(new BouncyCastleProvider());  注册 第三方库算法
 *
 * 2.Rsa 使用加密分段的作用
 *      RSA加密明文最大长度117字节，解密要求密文最大长度为128字节，所以在加密和解密的过程中需要分块进行。
 *      RSA加密对明文的长度是有限制的，如果加密数据过大会抛出如下异常：
 *          Exception in thread "main" javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
              at com.sun.crypto.provider.RSACipher.a(DashoA13*..)
              at com.sun.crypto.provider.RSACipher.engineDoFinal(DashoA13*..)
              at javax.crypto.Cipher.doFinal(DashoA13*..)
 **/
public class GenerateKeyPairUtils {

    private static final String ALGORITHM_RSA = "RSA";

    static
    {
        configureSecProvider();
    }

    /**
     * Configures security providers which are used by the library. Can be called
     * multiple times (subsequent calls won't have any effect).
     * <p>
     * This method must be called before any other usage of the code from canl API.
     */
    public static void configureSecProvider()
    {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * 生成RSA的公钥和私钥
     */
    public static Map<String, Object> initKey(GenerateKeyPairDto dto) throws Exception{

        Map<String,Object> map = Maps.newHashMap();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGenerator.initialize(dto.getSecretKeyBits(),new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取pkcs#8公钥 字节
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] publicKeyEncoded = publicKey.getEncoded();

        // 获取pkcs#8私钥 字节
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        byte[] privateKeyEncoded = privateKey.getEncoded();

        // 获取#1 公钥   并输入
        if(Const.SECRET_KEY_FORMAT[0].equals(dto.getSecretKeyFormat())){
            //  获取公钥
            // pkcs#1
            // 转 #1格式秘钥
            SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(publicKeyEncoded);
            ASN1Primitive primitive = spkInfo.parsePublicKey();
            // 获得#1格式秘钥
            byte[] primitivePublicKey = primitive.getEncoded();

            String pkc1_publicKey = buildResult(dto, primitivePublicKey);

            String hexPubKey = Hex2Util.encodeHex(pkc1_publicKey);


            // 获取私钥
            // 转#1
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyEncoded);
            ASN1Encodable asn1Encodable = privateKeyInfo.parsePrivateKey();
            ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
            byte[] primitiveEncodedPrivateKey = asn1Primitive.getEncoded();
            String pkcs1_privateKey = "";
            if(StringUtils.isNotBlank(dto.getSecretKey())){
                // 序列化私钥  需要用私钥密码
                // You must specify a password when serializing a private key
                // AES-128-CBC
                // DES-EDE3-CBC
                JcePEMEncryptorBuilder builder = new JcePEMEncryptorBuilder("AES-128-CBC");
                builder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
                builder.setSecureRandom(new SecureRandom());
                PEMEncryptor encryptor = builder.build(dto.getSecretKey().toCharArray());
                JcaMiscPEMGenerator gen = new JcaMiscPEMGenerator(keyPair.getPrivate(), encryptor);

              /*  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(new OutputStreamWriter(baos,
                        StandardCharsets.US_ASCII));

                jcaPEMWriter.writeObject(keyPair.getPrivate(),encryptor);
                jcaPEMWriter.flush();
                jcaPEMWriter.close();
                byte[] bytes = baos.toByteArray();
                System.out.println("测试："+new String(bytes));*/
                pkcs1_privateKey = out(gen.generate());
            }else {
               pkcs1_privateKey =  buildResult(dto, primitiveEncodedPrivateKey);
            }
            String hex_pkcs1_privateKey = Hex2Util.encodeHex(pkcs1_privateKey);
            buildMap(dto, map, pkc1_publicKey, hexPubKey, pkcs1_privateKey, hex_pkcs1_privateKey);

        }else {
            // #8
            // 获取公钥

            String spb = buildResult(dto, publicKeyEncoded);

            String hex_spb = Hex2Util.encodeHex(spb);

            // 获取私钥
            // 判断私钥是否需要进行密码加密
            String out = "";
            if(StringUtils.isNotBlank(dto.getSecretKey())){
                // 私钥 pkcs# 8 秘钥密码加密
                JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_RC2_128);
                encryptorBuilder.setRandom(new SecureRandom());
                encryptorBuilder.setPasssword(dto.getSecretKey().toCharArray());
                OutputEncryptor encryptor = encryptorBuilder.build();

                JcaPKCS8Generator gen2 = new JcaPKCS8Generator(keyPair.getPrivate(), encryptor);
                out = out(gen2.generate());
            }else {
                out = buildResult(dto, privateKeyEncoded);
            }

            String hexPriKey = Hex2Util.encodeHex(out);

            buildMap(dto, map, spb, hex_spb, out, hexPriKey);
        }
        return map;
    }



    private static void buildMap(GenerateKeyPairDto dto, Map<String, Object> map, String publicKey, String hex_publicKey, String privateKey, String hex_privateKey) {
        if (dto.getOutputFormat().equals(Const.RSA_OUTPUT_METHOD[0])) {
            map.put(Const.PUBLIC_KEY_RSA, publicKey);
            map.put(Const.PRIVATE_KEY_RSA, privateKey);
        } else if (dto.getOutputFormat().equals(Const.RSA_OUTPUT_METHOD[1])) {
            map.put(Const.PUBLIC_KEY_RSA, hex_publicKey);
            map.put(Const.PRIVATE_KEY_RSA, hex_privateKey);
        } else if (dto.getOutputFormat().equals(Const.RSA_OUTPUT_METHOD[2])) {
            map.put(Const.PUBLIC_KEY_RSA, hex_publicKey);
            map.put(Const.PRIVATE_KEY_RSA, privateKey);
        } else if (dto.getOutputFormat().equals(Const.RSA_OUTPUT_METHOD[3])) {
            map.put(Const.PUBLIC_KEY_RSA, publicKey);
            map.put(Const.PRIVATE_KEY_RSA, hex_privateKey);
        }
    }

    private static String buildResult(GenerateKeyPairDto dto, byte[] key) throws IOException {
        PemObject pemObject = new PemObject(dto.getHeadType(), key);
        return out(pemObject);
    }

    private static String out(PemObject pemObject) throws IOException {
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(stringWriter)) {
            pw.writeObject(pemObject);
        }
        return stringWriter.toString();
    }


}
