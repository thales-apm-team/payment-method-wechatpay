package com.payline.payment.wechatpay.bean.response;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.WeChatPayBean;
import com.payline.payment.wechatpay.bean.nested.Code;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public abstract class Response extends WeChatPayBean {

    @SerializedName("return_code")
    @NonNull
    private final Code returnCode;

    @SerializedName("return_msg")
    private final String returnMessage;

    @SerializedName("result_code")
    @NonNull
    private final Code resultCode;

    // error fields
    @SerializedName("error_code")
    private final String errorCode;

    @SerializedName("error_code_des")
    private final String errorCodeDescription;
}
