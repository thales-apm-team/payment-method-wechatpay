package com.payline.payment.wechatpay.util.security;

import com.github.wxpay.sdk.WXPayConstants;
import com.payline.payment.wechatpay.util.JsonService;
import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@UtilityClass
public class SignatureUtil {
    private static final String FIELD_SIGN = "sign";

    private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new SecureRandom();


    public static boolean isSignatureValid(Map<String, String> data, String key) {
        if (!data.containsKey(FIELD_SIGN)) {
            return false;
        }
        String sign = data.get(FIELD_SIGN);
        return generateSignature(data, key).equals(sign);
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data Map类型数据
     * @param key  API密钥
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key) {
        String sign = generateSignature(data, key);
        data.put(FIELD_SIGN, sign);
        return JsonService.getInstance().mapToXml(data);
    }


    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param key  API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key) {
        StringBuilder sb = new StringBuilder(
                data.entrySet().stream()
                .filter(e -> !e.getKey().equals(FIELD_SIGN))        // remove signature entry
                .filter(e -> e.getValue().trim().length() > 0)  // remove empty entries
                .sorted(Map.Entry.comparingByKey())             // sort entry by alphabetical keys
                .map(e -> e.getKey() + "=" + e.getValue().trim())// create URL encoded String with remaining entries
                .collect(Collectors.joining("&"))
        );
        sb.append("key=").append(key);                           // add key to the end of created String
        return HMACSHA256(sb.toString(), key);
    }


    /**
     * 获取随机字符串 Nonce Str
     *
     * @return String 随机字符串
     */
    public static String generateNonceStr() {
        char[] nonceChars = new char[32];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    /**
     * 生成 HMACSHA256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // should never append
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null; // todo a changer
    }

}
