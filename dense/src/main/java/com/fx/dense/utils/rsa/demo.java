package com.fx.dense.utils.rsa;

import cn.hutool.core.util.HexUtil;
import com.fx.dense.utils.Hex2Util;
import org.assertj.core.util.Strings;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import sun.misc.BASE64Decoder;

import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-10 13:39
 **/
public class demo {


    public static void main(String[] args) throws Exception {

        String aa = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "Proc-Type: 4,ENCRYPTED\n" +
                "DEK-Info: AES-128-CBC,C3A46E49A4439B459CD9AA4262FCF2D1\n" +
                "\n" +
                "ZASnAc4BP08E3xYKplIWUX/RKbjUhatbvFldYJryu5fH+aR/O2DI4q7rp2Rd8133\n" +
                "n9hrE8M4/91783zhP0kYGdV5ku7M3XxYlEns/Llr1ZBZUMtgdam4B3QZXwMnZFXa\n" +
                "uw8Ucx8Jw+SqzHZlaKpVuYXKhWTtaZFyk/l4OVzYaKFlQ9ztVmRoy1iJoZ/na2/y\n" +
                "djoGwUTVcueckKxLS6J6sRt+h3xgS6qPeFp6JNrmBfwO/Zp5WlKaPWok0F9Zv2k3\n" +
                "7rgESFvOt+H/EGbKmaGrjmhuIql9ShjywZsKhbLRmJULCxofXrJozS2+xkc6rlEG\n" +
                "DUAAsXV3y4v24V78rrwsKn46lw6ZILOBt0qGsHunC1u7x2iDKPjZQNl9aUDc1iKo\n" +
                "KzioWOzWlzSeI+sTUjfcHO2sqtuyV1VvILQCgh/gGZkqmvb2ah+bID92i021vvqI\n" +
                "MV6Bf9T2eIy9PdVoKjus3tOKYT5x6Fs5FBNh2moBLfgUXtHaCYoaOMyQ4Gdn/lwl\n" +
                "IryZy2LsEBU7qU4vcXynGtkHZhzdtTp1MCPfq4vygeSrgLtwbxkaeSWaV2rBVCAH\n" +
                "Zt2MbhnckES/7Byub6A2XQkk7qrbKmHEHTdcrQm1l57+ycYAzP5lEU7vFRnWgrlR\n" +
                "xUj8tcZmr0/GiqMDm5uu0BwSjL6LfS8jIdDlBf9gLLrRWHgNmAu0gq/zZzmmuLd1\n" +
                "oouOy3YuK9cdxw1r3S2HM/0YqcXXHE+eWZUk1A6ZdGxSFBkFrXUFsN14uKPk6Vs/\n" +
                "/sPMBjfZU7c1P36cUjAJGU5ZrftsaD1VDcobgMElGjrtfL1ulE331+AhhQP7996U\n" +
                "-----END RSA PRIVATE KEY-----" ;
        System.out.println(aa);

        aa = aa.replace("-----BEGIN RSA PRIVATE KEY-----", "");
        aa = aa.replace("-----END RSA PRIVATE KEY-----", "");

        EncryptedPrivateKeyInfo pkInfo = new EncryptedPrivateKeyInfo(Base64.decode(aa));
        PBEKeySpec keySpec = new PBEKeySpec("11".toCharArray());
        SecretKeyFactory pbeKeyFactory = SecretKeyFactory.getInstance(pkInfo.getAlgName());
        PKCS8EncodedKeySpec encodedKeySpec = pkInfo.getKeySpec(pbeKeyFactory.generateSecret(keySpec));
        KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory1.generatePrivate(encodedKeySpec);
        // 私钥先通过  ‘私钥解密’ 后才可以进行文本解密
        // 得到私钥解密后的私钥字节数组
        byte[] encoded = privateKey.getEncoded();

        PemObject pemObject = new PemObject("", encoded);

        StringWriter stringWriter = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(stringWriter)) {
            pw.writeObject(pemObject);
        }
        System.out.println(stringWriter.toString());




        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        //3. base64解码 找到原秘钥
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(aa);
        //4.得到原公钥数组
        byte[] bytes1 = pkcs1ToPkcs8(true, bytes);

        PemObject pemObject1 = new PemObject("", bytes1);

        StringWriter stringWriter1 = new StringWriter();
        try (JcaPEMWriter pw = new JcaPEMWriter(stringWriter1)) {
            pw.writeObject(pemObject1);
        }
        System.out.println(stringWriter1.toString());


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

        ASN1Primitive asn1Primitive = new ASN1Enumerated(buffer);
        if (isPrivateKey) {
            PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, asn1Primitive);
            return privateKeyInfo.getEncoded();
        } else {
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(algorithmIdentifier, asn1Primitive);
            return subjectPublicKeyInfo.getEncoded();
        }
    }

}
