package com.payline.payment.wechatpay.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
public class UnifiedOrderRequest extends Request{
    String body;

    @SerializedName("out_trade_no")
    String outTradeNo;

    @SerializedName("device_info")
    String deviceInfo;

    @SerializedName("fee_type")
    String feeType;

    @SerializedName("total_fee")
    String totalFee;

    @SerializedName("spbill_create_ip")
    String spBillCreateIp;

    @SerializedName("notify_url")
    String notifyUrl;

    @SerializedName("trade_type")
    String tradeType;

    @SerializedName("product_id") String productId;
}
