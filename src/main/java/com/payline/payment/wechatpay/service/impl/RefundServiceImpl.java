package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.SubmitRefundRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.service.RefundService;

public class RefundServiceImpl implements RefundService {
    HttpService httpService = HttpService.getInstance();

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        RequestConfiguration configuration = RequestConfigurationService.getInstance().build(refundRequest);


        SubmitRefundRequest submitRefundRequest = SubmitRefundRequest.builder()
                .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                .nonceStr(PluginUtils.generateRandomString(32))
                .signType(SignType.valueOf(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))

                .transactionId(refundRequest.getPartnerTransactionId())
                .outTradeNo(refundRequest.getTransactionId())
                .totalFee(refundRequest.getAmount().getAmountInSmallestUnit().toString())
                .refundFee(refundRequest.getAmount().getAmountInSmallestUnit().toString())
                .refundFeeType(refundRequest.getAmount().getCurrency().getCurrencyCode())
                .build();

        httpService.submitRefund(configuration, submitRefundRequest);
        return null;
    }

    @Override
    public boolean canMultiple() {
        return true;
    }

    @Override
    public boolean canPartial() {
        return true;
    }
}
