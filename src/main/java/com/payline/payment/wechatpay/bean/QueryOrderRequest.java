package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
public class QueryOrderRequest extends Request {
    @SerializedName("device_info")
    String deviceInfo;

    @SerializedName("transaction_id")
    String transactionId;

    @SerializedName("out_trade_no")
    String outTradeNo;


}
