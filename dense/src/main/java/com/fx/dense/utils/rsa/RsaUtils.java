package com.fx.dense.utils.rsa;

import cn.hutool.core.util.HexUtil;
import com.fx.dense.base.Const;
import com.fx.dense.model.rsa.GenerateKeyPairDto;
import com.fx.dense.model.rsa.PublicKeyEncryptionDto;
import com.fx.dense.utils.Hex2Util;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.assertj.core.util.Lists;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DLSequence;
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
     * @param usePKCS8  是否采用PKCS8填充模式
     * @param publicKey 公钥
     * @return 公钥
     * @throws Exception 加密过程中的异常信息
     */
    public static RSAPublicKey generatePublicKey(boolean usePKCS8, byte[] publicKey) throws Exception {
        KeySpec keySpec;
        if (usePKCS8) {
            // PKCS8填充
            keySpec = new X509EncodedKeySpec(publicKey);
        } else {
            // PKCS1填充
            DLSequence sequence = (DLSequence) ASN1Primitive.fromByteArray(publicKey);
            BigInteger v1 = ((ASN1Integer) sequence.getObjectAt(0)).getValue();
            BigInteger v2 = ((ASN1Integer) sequence.getObjectAt(1)).getValue();
            keySpec = new RSAPublicKeySpec(v1, v2);
        }
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(keySpec);
        return pubKey;
    }


    public static byte[] publicKeyEncryption(String context, String publicKey, boolean type) {

        try {
            byte[] contextBytes = context.getBytes();

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

            //3. base64解码 找到原秘钥
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(publicKey);
            //4.得到原公钥数组

            RSAPublicKey key = generatePublicKey(true, bytes);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            int inputLen = contextBytes.length;
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

    public static List<String> pkcs1containSecret() {

        String publicKey = "-----BEGIN -----\n" +
                "MIGJAoGBAJgVdNY4X5FHMq5xNKfTAOL3jVHks/RvioJ7uxNVhkpu/uwaiumCwzvn\n" +
                "Jz4GnGUBGfwAdqS3PaLHBHo60n1Kt1pXVVV1nLCxJtfsHZIxcE4RDXKBe57xP7Ah\n" +
                "QTQ3aSbCgYSQATwEsqzW6aGauG/jwZfxRmBD5H0bSRYdvDQ53mwBAgMBAAE=\n" +
                "-----END -----";
        String privateKey = "-----BEGIN -----\n" +
                "MIICWwIBAAKBgQCYFXTWOF+RRzKucTSn0wDi941R5LP0b4qCe7sTVYZKbv7sGorp\n" +
                "gsM75yc+BpxlARn8AHaktz2ixwR6OtJ9SrdaV1VVdZywsSbX7B2SMXBOEQ1ygXue\n" +
                "8T+wIUE0N2kmwoGEkAE8BLKs1umhmrhv48GX8UZgQ+R9G0kWHbw0Od5sAQIDAQAB\n" +
                "AoGAJ00kLFfVGo3zovDOUrBMglrGwmr/tiM9AAtJhO2NDp8wcYNKcp3AJjLOCVFc\n" +
                "CR4HwP+9qUNRQkd7+LpKuuYcC22tdAuR/xGYxuW2/Wsnu5qOFmj0EAE6892k8dDj\n" +
                "RuaQwggzv4Rjr4Pv8z48LdSYfBW5bPt3ooV/mRKlQN+ay4ECQQDhyEVTWo2dYqZQ\n" +
                "PtF/1dUVNHK8bLR9OFG8PjxtySL62vKRwblQOa3+UvaFDK8cCNB9vV0/ulWGFczn\n" +
                "xFKmmHJVAkEArHAiva16sbKm7gK9wlWQSbLFnnIPf1niUM4CtCTqW4xSAkWumVY7\n" +
                "EqQafBqUoWns2C7hkRgqxS0E+YfZwzm2/QJAS12CZpRveP2Y7mhJnhZOjkl3kxXm\n" +
                "GXZXMjLEERF2r62uEqFLrk/SmHYw+7CEMyNuFMrE+aTFL4DPaP3LaPiyEQJAe5iM\n" +
                "biPv83o3yBVS6f3mQ8zNdLoQfZlxa7Wdnn1vNVsoVNSZRvLVuJDDIvzyV5fS2UkR\n" +
                "CKyny1hvXmOPJC00CQJAPaY4ubgnyQE58ueSgO4s+9CMKbRJUU49omOnEGRsqOU4\n" +
                "SPW4cMJAeiTp2dX1zwuohw9CiDjIJ8ZjJB0IDkrRng==\n" +
                "-----END -----";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }

    public static List<String> pkcs1containSecretToPrivateKeyAndPublicKeyHex() {

        String publicKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d49474a416f4742414f4c666c674a2f77717455625558594d594e45503675646968394879684b6d63566f4a6f3252483730593435594e4946747a5a774439350d0a63686b51672f6f7575416744374c467a61684d75303062554754376f664654414d4a69446b4748456e4d506561346a6f4c474e49414375796130584f582b63510d0a563135694944314a657772323578496c566e5a7276554c342b375579676879624f316f43375748704262536973522f663430743341674d424141453d0d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        String privateKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d4949435867494241414b42675144693335594366384b72564731463244474452442b726e596f6652386f53706e466143614e6b522b39474f4f5744534262630d0a3263412f6558495a454950364c726749412b797863326f544c744e4731426b2b364878557744435967354268784a7a44336d75493643786a53414172736d74460d0a7a6c2f6e45466465596941395358734b397563534a565a32613731432b5075314d6f49636d7a746141753168365157306f724566332b4e4c64774944415141420d0a416f4741526e587558775253373263664d4f762b4a4b695470626364364b3068497a327951715a76716e7430347268552f37727037746c714463796f4651532b0d0a474874654349382f656256416f6f694f635a6235364548494b757868387449526e526f6a4a514746424b4b2b397a422f5a6d312b71572f3438716879686e67540d0a3853556938356c536875477863716c773352614e706647376d4d3666343679346d58474a58387050376437636b354543515144353738486c796e5473574752350d0a4c4879707847725a776b614d41545955306a5973474545434b2f476f7a45416b6a70556c6473625a7475394e6b3351537757584161725043393250634f7158690d0a345464374d704437416b4541364743582b78455370394466624d52716d697a37753439696951684d5857365244614c71314e4677305767467a6c36666e42536f0d0a513678784e5757687a544e6335675a3645675a64746a6d7958426f706a7a492b74514a42414933546e4a374e2f50316b78316276684b6f3957446a4c676f624e0d0a355364356864344e7755332f4b376d5364632b497a562b6541416270525041726656467252316368395848734f6c4674516c62315943746272574d435151446a0d0a4454615376327a4f4168323870664a504d4c4c4b34642f796175524b796e54367350766f383766624e467576392b375754367a56617447327a64595a725230520d0a4f4c54666c63374d395855623377684b66567164416b4541374148356662616b493065797539346a717268783378346f46672b34477249366d453670656255520d0a71464463336d754a512b39657573322f3535614a4c706978464f586b36332b732b4539366b31504837414f6168773d3d0d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }


    public static List<String> pkcs1containSecretToPrivateKeyHex() {

        String publicKey = "-----BEGIN -----\n" +
                "MIGJAoGBAOeNWYF9bXPiW7Nf1TXR2nG8MZ7ZrJVr/xeDeBdAFRwcKOqx1HC3taPF\n" +
                "7rMHeSBlQYQGsBUgxYwBcispNJhGPX+2c5wQ5qLBkf1L0JXxoQ0FSs6pURc6Ywe3\n" +
                "oWGsZxZjfvkUW/wdGsPbbE+bnzl2nVDTF4WxvdXkUGrwOYxJXlglAgMBAAE=\n" +
                "-----END -----";
        String privateKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d4949435841494241414b426751446e6a566d426657317a346c757a58395531306470787644476532617956612f3858673367585142556348436a71736452770d0a7437576a7865367a42336b675a55474542724156494d574d415849724b545359526a312f746e4f63454f6169775a4839533943563861454e4255724f715645580d0a4f6d4d487436466872476357593337354646763848527244323278506d3538356470315130786546736233563546427138446d4d535635594a514944415141420d0a416f4741577047644b7473476a7364424776346e425441614131616243676b57536a4f3979564b784446635361725a52417375667a70377375797857573078580d0a416d62596f3232435069466459342f464f6b435a7739336c653270483430464e4b4843537538576d4767525262674b44653566624d34534971634161512f52410d0a5159712b58664e504b4c416c554e6946494c7a782b336c783456432b6a76354435494d63665a37324c6731436a316b435151443161516e5346365a69652b61420d0a322b413978774c546b784846746738444971344e382b773166576a3561674b4337793035456f61674b78314359612b653538667943576a3057666a793265786a0d0a33474e5a464e4d58416b4541385973356e48584556427677453552534e654a7942464b3241543041314171554c57776c4945514b4765336a57316944344a6a420d0a344837586d37367578333943574c657263536d5875614343336a655a6738506b49774a42414b596e322f4a572b4e77744c304130386e7068523953662b7152320d0a51327649437a682f4d69703149714862324a39312b4a52767170362f4569786a6e31686a626369392f6b57537453385878536c486969784349546b43514439440d0a644f4a713466495a6f796174715636646f626c4b4a497473652b514e70555045574844384152314c646b6e396841543258596b3844364b6d4c63626f714f72740d0a71455371585366735233714757336e73653773435145447337766275764852776e5134682f7858453847537152583849466c4c2f5851574379486169665a72310d0a76344c517a61366e6b7233376d5a726343414655586b464d6b2f7846315668582b7a624139362f664672493d0d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }

    public static List<String> pkcs1containSecretToPublicKeyHex() {

        String publicKey = "2d2d2d2d2d424547494e202d2d2d2d2d0d0a4d49474a416f474241497138414f577a73456c43746856722f464778504a6268527243514a4f597735593730474b7746352b3357757a746438664573587944730d0a3750425372336439694e4b74463268644356732b3037636e386137394b52376f457277764d37792f467672583177455133483559674963715263636d4949596b0d0a5758457a634271554e30704875484d7946342f4f4f44394843324972696a68544952773468766d736269694e4c4334446d4f6d7241674d424141453d0d0a2d2d2d2d2d454e44202d2d2d2d2d0d0a";
        String privateKey = "-----BEGIN -----\n" +
                "MIICXQIBAAKBgQCKvADls7BJQrYVa/xRsTyW4UawkCTmMOWO9BisBeft1rs7XfHx\n" +
                "LF8g7OzwUq93fYjSrRdoXQlbPtO3J/Gu/Ske6BK8LzO8vxb619cBENx+WICHKkXH\n" +
                "JiCGJFlxM3AalDdKR7hzMhePzjg/RwtiK4o4UyEcOIb5rG4ojSwuA5jpqwIDAQAB\n" +
                "AoGAaWf4Ao3olXDjKRl3hpXzo+sbK1EJR//Emj1pdWGzWmg4rx1skkGVMU3xo5If\n" +
                "ENlHGFI8o6V0U2hDsTffD4X3NAQ0TZ9WkTWjY8u2swKsa/2zIRK9iC4SSA5RurIB\n" +
                "hcJFfCHEApk1LLl+Q25/5Zyp3BSDh38lWM6d9pSresAg7LECQQD3YJ4Yl99Mp9vm\n" +
                "zBWcY6x5upYyaoRLt8KO9/KSnC60OwedYWGXCOHIq0PcnRCQY9XAWLuC+0Hq0oph\n" +
                "tcxYXMgjAkEAj5HzDB1QizzY3tcHuZgkabSdxxk8DvS4KrH0pyaFwTFnX3ThlxGh\n" +
                "l33E+IOH+OYa2bwF8QK70BzVLvP7H3Hs2QJAKhwUTXNs24unbz5GX3zIG2CbuLFR\n" +
                "G+KIB/ZFIJfdi2iQ/0VYa3FjndkpkCBcdXfJJxjzsnQ64FI+pCtiQZhpqQJBAIy9\n" +
                "8NtxaWH11kwt0/7W7OtLClkBneSzdk80gLfThc+sFMB5HiUwPY761jshBgyz1qKY\n" +
                "NeLcYS1U9o++0fEzh5ECQQDhrCYSYD2gOex72q7O/86527sqtvuYFLSAIya9ZrbT\n" +
                "V4uReRHhHlkMK9daY3uozlwsXmgUttBfC9hl57yr3RaR\n" +
                "-----END -----";
        List<String> list = Lists.newArrayList();
        list.add(publicKey);
        list.add(privateKey);
        return list;
    }

    public static void main(String[] args) {
        List<String> my = null;
        //1.1完成测试  pkcs8  base64 输出
         my = pkcs8containSecretToBase64();
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




        PublicKeyEncryptionDto build = PublicKeyEncryptionDto.builder()
                .publicKey(my.get(0))
                .privateKey(my.get(1))
                .secretKey("")
                .output(Const.RSA_OUTPUT_METHOD[2])
                .text("七龙珠")
                .build();

        try {
            // 公钥加密
            String enResult = publicKeyEncryptedByteToString(build);
            // 私钥解密
            privateKeyDecryption(build, enResult);


        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    private static void privateKeyDecryption(PublicKeyEncryptionDto dto, String enResult) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        String privateKey = dto.getPrivateKey();

        //1. 如果为hex 这样转为base64
        if (privateKey.matches(Const._HEXADECIMAL_REGULAR_16)) {
            privateKey = Hex2Util.decodeHex(privateKey);
        }

        // 1.文本需要通过base64解密 获得 字节数组  进行解密
        byte[] decode = java.util.Base64.getDecoder().decode(enResult);
        PrivateKey encryptedPrivateKey = null;
        byte[] encoded = null;
        String secretKey = dto.getSecretKey();

        if(StringUtils.isNotBlank(dto.getSecretKey())){
            if(privateKey.contains("Proc-Type: 4,ENCRYPTED")){
                System.out.println("暂不支持pkcs1下 （已加密私钥）密码的 解密");
                return;
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

        // 解密操作
        byte[] decryptedData = getDecryptedData(decode, encoded);

        System.out.println(new String(decryptedData));
    }


    private static String publicKeyEncryptedByteToString(PublicKeyEncryptionDto dto) {

        // type true pkcs8   false pkcs1

        boolean type = false;

        byte[] bytes = publicKeyEncryption(dto.getText(), dto.getPublicKey(), type);

        // 通过base64 再次将字节数组进行base64编码 得到String字符串文本
        String byte2Base64 = java.util.Base64.getEncoder().encodeToString(bytes);
        return byte2Base64;
    }

    private static byte[] getDecryptedData(byte[] decode, byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
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
        return decryptedData;
    }


    private static byte[] getEncryptedData(String publicKey, byte[] contextBytes) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        if (HexUtil.isHexNumber(publicKey)) {
            publicKey = Hex2Util.decodeHex(publicKey);
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(publicKey);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = contextBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
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
