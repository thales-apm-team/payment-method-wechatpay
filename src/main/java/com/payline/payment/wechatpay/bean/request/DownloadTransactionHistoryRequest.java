package com.payline.payment.wechatpay.bean.request;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.wechatpay.bean.WeChatPayBean;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Value
@EqualsAndHashCode(callSuper = true)
public class DownloadTransactionHistoryRequest extends WeChatPayBean {

    @SerializedName("bill_date")
    String billDate;

    @SerializedName("bill_type")
    String billType;
}
