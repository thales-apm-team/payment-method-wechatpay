package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class Response {

    @SerializedName("return_code")
    @NonNull
    private final String returnCode;

    @SerializedName("return_message")
    private final String returnMessage;

    @SerializedName("result_code")
    @NonNull
    private final String resultCode;

    // error fields
    @SerializedName("error_code")
    private final String errorCode;

    @SerializedName("error_code_des")
    private final String errorCodeDescription;

    @SerializedName("prepay_id")
    private final String prepayId;

    @SerializedName("code_url")
    private final String codeUrl;
}
