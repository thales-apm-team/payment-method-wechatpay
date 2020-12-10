package com.github.wxpay.sdk;


import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
public class CustomConfig extends WXPayConfig {
    private final String appID;
    private final String mchID;
    private final String key;
    private final InputStream certStream;

    public CustomConfig(RequestConfiguration configuration) {
        this.appID = "111";
        this.mchID = "222";
        this.key = "333";
        this.certStream = new ByteArrayInputStream("salut".getBytes(StandardCharsets.UTF_8));

    }


    @Override
    IWXPayDomain getWXPayDomain() {
        return null;
    }
}
