package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.nested.SignType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
@EqualsAndHashCode
public abstract class WeChatPayBean {
    @NonNull
    @SerializedName("appid")
    String appId;

    @NonNull
    @SerializedName("mch_id")
    String merchantId;

    @SerializedName("sub_appid")
    String subAppId;

    @NonNull
    @SerializedName("sub_mch_id")
    String subMerchantId;

    @SerializedName("device_info")
    String deviceId;

    @NonNull
    @SerializedName("nonce_str")
    String nonceStr;

    @SerializedName("sign_type")
    SignType signType;

    String sign;
}
