package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.nested.SignType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Getter
public abstract class Request {
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

    @NonNull
    @SerializedName("nonce_str")
    String nonceStr;

    @NonNull
    @SerializedName("sign_type")
    SignType signType;
    String sign;    // todo voir si on garde ce champ signature
}
