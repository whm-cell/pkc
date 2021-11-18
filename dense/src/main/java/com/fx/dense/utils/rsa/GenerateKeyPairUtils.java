package com.fx.dense.utils.rsa;




import com.alibaba.fastjson.JSONObject;
import com.fx.dense.base.Const;
import com.fx.dense.model.rsa.GenerateKeyPairDto;
import com.google.common.collect.Maps;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.util.io.pem.PemObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
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

    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 生成RSA的公钥和私钥
     */
    public static Map<String, Object> initKey(GenerateKeyPairDto dto) throws Exception{


        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        keyPairGenerator.initialize(dto.getSecretKeyBits(),new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // 获取pkcs#8公钥 字节
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        byte[] publicKeyEncoded = publicKey.getEncoded();

        // 获取pkcs#8私钥 字节
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        byte[] privateKeyEncoded = privateKey.getEncoded();

        //encrypted form of PKCS#8 file
        JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(PKCS8Generator.PBE_SHA1_RC2_128);
        encryptorBuilder.setRandom(new SecureRandom());
        encryptorBuilder.setPasssword("abcde".toCharArray());
        OutputEncryptor encryptor = encryptorBuilder.build();



        // 获取#1 公钥   并输入
        if(Const.SECRET_KEY_FORMAT[0].equals(dto.getSecretKeyFormat())){
            //  获取公钥
            // pkcs#1
            // 转 #1格式秘钥
            SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(publicKeyEncoded);
            ASN1Primitive primitive = spkInfo.parsePublicKey();
            // 获得#1格式秘钥
            byte[] primitivePublicKey = primitive.getEncoded();

            String 公钥 = buildResult(dto, primitivePublicKey);

            System.out.println(公钥);

            // 获取私钥
            // 转#1
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyEncoded);
            ASN1Encodable asn1Encodable = privateKeyInfo.parsePrivateKey();
            ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
            byte[] primitiveEncodedPrivateKey = asn1Primitive.getEncoded();
            String 私钥 = buildResult(dto, primitiveEncodedPrivateKey);

            System.out.println(私钥);

        }else {
            // #8
            // 获取公钥
            String spb = buildResult(dto, publicKeyEncoded);

            System.out.println(spb);

            // 获取私钥
            String spv = buildResult(dto, privateKeyEncoded);

            System.out.println(spv);

        }



        return null;
    }

    private static String buildResult(GenerateKeyPairDto dto, byte[] key) throws IOException {
        PemObject pemObject = new PemObject(dto.getHeadType(), key);
        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(stringWriter)) {
            pw.writeObject(pemObject);
        }
        return stringWriter.toString();
    }


}
