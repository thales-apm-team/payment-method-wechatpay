package com.payline.wechatpay;

import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;

import java.math.BigInteger;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MockUtils {

    /**
     * Generate a valid Payline Amount.
     */
    public static com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(10), Currency.getInstance("EUR"));
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }
    /**
     * Generate a valid {@link ContractConfiguration} to verify the connection to the API.
     */
    public static ContractConfiguration aContractConfiguration() {

        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(ContractConfigurationKeys.MERCHANT_ID, new ContractProperty("MERCHANTID"));
        contractProperties.put(ContractConfigurationKeys.SUB_MERCHANT_ID, new ContractProperty("SUBMERCHANTID"));

        return new ContractConfiguration("WeChatPay", contractProperties);
    }
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(PartnerConfigurationKeys.APPID, "123456789");
        partnerConfigurationMap.put(PartnerConfigurationKeys.CERTIFICATE, "Certificat");
        partnerConfigurationMap.put(PartnerConfigurationKeys.DEVICE_INFO, "WEB");
        partnerConfigurationMap.put(PartnerConfigurationKeys.DOWNLOAD_TRANSACTIONS_URL, "www.google.fr");
        partnerConfigurationMap.put(PartnerConfigurationKeys.QUERY_ORDER_URL, "https://api.mch.weixin.qq.com/pay/orderquery");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SUBMIT_REFUND_URL, "https://api.mch.weixin.qq.com/secapi/pay/refund");
        partnerConfigurationMap.put(PartnerConfigurationKeys.UNIFIED_ORDER_URL, "https://api.mch.weixin.qq.com/pay/unifiedorder");
        partnerConfigurationMap.put(PartnerConfigurationKeys.KEY, "");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SUB_APPID, "");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SIGN_TYPE, "");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }
}