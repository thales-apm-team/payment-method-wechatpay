package com.payline.payment.wechatpay.bean.response;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.nested.RefundStatus;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
public class QueryRefundResponse extends Response{
    @SerializedName("refund_status")
    RefundStatus refundStatus;


}
