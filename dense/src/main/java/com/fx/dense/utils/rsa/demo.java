package com.fx.dense.utils.rsa;

import cn.hutool.core.util.HexUtil;
import com.fx.dense.utils.Hex2Util;
import org.assertj.core.util.Strings;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
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
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-10 13:39
 **/
public class demo {


    public static void main(String[] args) throws Exception {

        String aa = "-----BEGIN -----\n" +
                "MIGJAoGBAKd+NgIDUMvTIzvfsrL47y0qFgsCVG6YMyVrGvuXo6zzXRi2Dc6FnHE7\n" +
                "zU0EsSRQtPnO0oKOX8h7tv4DQdyBYLNA1xp/NxyamAZpCKdVdxndI5FUC7TmQ7l7\n" +
                "RSaRsK20BvdTF9Lc35/KFsiRCHupXmnqgLTqbOxd8KuQminUK5pFAgMBAAE=\n" +
                "-----END -----";

        aa = aa.replace("-----BEGIN -----","");
       aa =  aa.replace("-----END -----","");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(aa);


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
