package com.payline.payment.wechatpay;

import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.*;

@UtilityClass
public class MockUtils {
    public final String TRANSACTION_ID = "123456789012345678901";
    public final String PARTNER_TRANSACTION_ID = "098765432109876543210";

    /**
     * Generate a valid Payline Amount.
     */
    public com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(10), Currency.getInstance("EUR"));
    }

    /**
     * Generate a valid, but not complete, {@link Order}
     */
    public Order aPaylineOrder() {
        List<Order.OrderItem> items = new ArrayList<>();

        items.add(Order.OrderItem.OrderItemBuilder
                .anOrderItem()
                .withReference("foo")
                .withAmount(aPaylineAmount())
                .withQuantity((long) 1)
                .build());

        return Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withItems(items)
                .withReference("ORDER-REF-123456")
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public Buyer aBuyer() {
        return Buyer.BuyerBuilder.aBuyer()
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withEmail("foo@bar.baz")
                .build();
    }


    /**
     * Generate a valid {@link ContractConfiguration} to verify the connection to the API.
     */
    public ContractConfiguration aContractConfiguration() {

        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(ContractConfigurationKeys.MERCHANT_ID, new ContractProperty("MERCHANTID"));
        contractProperties.put(ContractConfigurationKeys.SUB_MERCHANT_ID, new ContractProperty("SUBMERCHANTID"));

        return new ContractConfiguration("WeChatPay", contractProperties);
    }

    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(PartnerConfigurationKeys.APPID, "123456789");
        partnerConfigurationMap.put(PartnerConfigurationKeys.CERTIFICATE, "Certificat");
        partnerConfigurationMap.put(PartnerConfigurationKeys.DEVICE_INFO, "WEB");
        partnerConfigurationMap.put(PartnerConfigurationKeys.QUERY_ORDER_URL, "https://api.mch.weixin.qq.com/pay/orderquery");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SUBMIT_REFUND_URL, "https://api.mch.weixin.qq.com/secapi/pay/refund");
        partnerConfigurationMap.put(PartnerConfigurationKeys.UNIFIED_ORDER_URL, "https://api.mch.weixin.qq.com/pay/unifiedorder");
        partnerConfigurationMap.put(PartnerConfigurationKeys.QUERY_REFUND_URL, "https://api.mch.weixin.qq.com/pay/queryrefund");
        partnerConfigurationMap.put(PartnerConfigurationKeys.DOWNLOAD_TRANSACTIONS_URL, "https://api.mch.weixin.qq.com/pay/downloadbill");
        partnerConfigurationMap.put(PartnerConfigurationKeys.KEY, "key");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SUB_APPID, "");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SIGN_TYPE, "MD5");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    /**
     * Generate a valid {@link Environment}.
     */
    public Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public Map<String, String> anAccountInfo() {
        return anAccountInfo(aContractConfiguration());
    }

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }


    // Request creation methods

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }


    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder() {
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(anAccountInfo())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    /**
     * Generate a builder for a valid {@link RefundRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public RefundRequest.RefundRequestBuilder aRefundRequestBuilder() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTION_ID)
                .withPartnerTransactionId(PARTNER_TRANSACTION_ID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }
}