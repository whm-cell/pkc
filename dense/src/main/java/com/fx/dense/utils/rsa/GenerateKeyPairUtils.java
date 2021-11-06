package com.fx.dense.utils.rsa;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
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



    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put("aa", publicKey);
        keyMap.put("bb", privateKey);
        return keyMap;
    }

    public static void main(String[] args) throws Exception {

        Map<String, Object> map = genKeyPair();

        map.forEach((k,v)->{

            System.out.println(k+"     "+v);

        });



       /* KeyPairGenerator rsa1 = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        rsa1.initialize(1024, new SecureRandom());
        KeyPair keyPair = rsa1.generateKeyPair();

        PublicKey pub = keyPair.getPublic(); // pkcs8公钥
        byte[] pubBytes = pub.getEncoded();
        SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pubBytes);
        ASN1Primitive primitive = spkInfo.parsePublicKey();
        byte[] publicKeyPKCS1 = primitive.getEncoded(); // pkcs1公钥




        System.out.println(publicKeyPKCS1);


        PrivateKey priv = keyPair.getPrivate(); // pkcs8私钥
        byte[] privBytes = priv.getEncoded();
        PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privBytes);
        ASN1Encodable encodable = pkInfo.parsePrivateKey();
        ASN1Primitive primitive1 = encodable.toASN1Primitive();
        byte[] privateKeyPKCS1 = primitive1.getEncoded(); // pkcs1私钥

        System.out.println(privateKeyPKCS1);*/

    }



}
