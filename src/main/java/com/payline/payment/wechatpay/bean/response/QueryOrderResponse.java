package com.payline.payment.wechatpay.bean.response;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.nested.TradeState;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
@ToString
public class QueryOrderResponse extends Response{
    @SerializedName("trade_state")
    @NonNull
    TradeState tradeState;
}
