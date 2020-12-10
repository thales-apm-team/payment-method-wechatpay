package com.payline.payment.wechatpay.bean.response;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
@ToString
public class SubmitRefundResponse extends Response{

    @SerializedName("refund_id")
    String refundId;
}
