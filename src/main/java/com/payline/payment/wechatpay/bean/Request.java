package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Getter
public abstract class Request {
    @SerializedName("appid")
    String appId;
    @SerializedName("mch_id")
    String merchantId;

    @SerializedName("sub_appid")
    String subAppId;
    @SerializedName("sub_mch_id")
    String subMerchantId;

    @SerializedName("nonce_str")
    String nonceStr;

    @SerializedName("sign_type")
    String signType;
    String sign;
}
