package com.payline.payment.wechatpay.util.security;

import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.util.JsonService;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class SignatureUtil {
    private static final String FIELD_SIGN = "sign";


    public static boolean isSignatureValid(Map<String, String> data, String key, SignType signType) {
        if (!data.containsKey(FIELD_SIGN)) {
            return false;
        }
        String sign = data.get(FIELD_SIGN);
        return generateSignature(data, key, signType).equals(sign);
    }

    /**
     * 生成带有 sign 的 XML 格式字符串
     *
     * @param data Map类型数据
     * @param key  API密钥
     * @return 含有sign字段的XML
     */
    public static String generateSignedXml(final Map<String, String> data, String key, SignType signType) {
        String sign = generateSignature(data, key, signType);
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
    public static String generateSignature(final Map<String, String> data, String key, SignType signType) {
        StringBuilder sb = new StringBuilder(
                data.entrySet().stream()
                        .filter(e -> !e.getKey().equals(FIELD_SIGN))    // remove signature entry
                        .filter(e -> e.getValue().trim().length() > 0)  // remove empty entries
                        .sorted(Map.Entry.comparingByKey())             // sort entry by alphabetical keys
                        .map(e -> e.getKey() + "=" + e.getValue().trim())// create URL encoded String with remaining entries
                        .collect(Collectors.joining("&"))
        );
        sb.append("&key=").append(key);                           // add key to the end of created String

        if (signType.equals(SignType.MD5)) {
            return hashWithMD5(sb.toString());
        } else {
            return hashWithSha256(sb.toString(), key);
        }
    }

    // todo a virer plus tard
    public String hashWithSha256(String data, String key) {
        try {
            // init cipher
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");    // todo passer ca en String
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8))).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null; // todo
    }

    public String hashWithMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            return Hex.encodeHexString(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null; // todo a changer

    }

}
