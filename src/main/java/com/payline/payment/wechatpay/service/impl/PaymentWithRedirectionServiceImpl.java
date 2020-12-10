package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.request.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.response.Response;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.service.PaymentWithRedirectionService;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    HttpService httpService = HttpService.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        RequestConfiguration configuration = RequestConfigurationService.getInstance().build(transactionStatusRequest);

        QueryOrderRequest queryOrderRequest = QueryOrderRequest.builder()
                .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                .deviceInfo("WEB")
                .transactionId(transactionStatusRequest.getTransactionId()) // todo verifier ca
                .nonceStr(PluginUtils.generateRandomString(32))
                .signType(SignType.valueOf( configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                .build();

        Response response = httpService.queryOrder(configuration, queryOrderRequest);

        return null;
    }
}
