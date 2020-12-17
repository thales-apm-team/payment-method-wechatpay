package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.nested.TradeState;
import com.payline.payment.wechatpay.bean.request.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.response.NotificationMessage;
import com.payline.payment.wechatpay.bean.response.QueryOrderResponse;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.JsonService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.payment.wechatpay.util.security.SignatureUtil;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.IgnoreNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BuyerPaymentId;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.NotificationService;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class NotificationServiceImpl implements NotificationService {
    JsonService jsonService = JsonService.getInstance();
    HttpService httpService = HttpService.getInstance();
    SignatureUtil signatureUtil = SignatureUtil.getInstance();

    @Override
    public NotificationResponse parse(NotificationRequest notificationRequest) {
        // init data
        PaymentResponse paymentResponse;
        NotificationResponse notificationResponse;
        String partnerTransactionId = "UNKNOWN";

        try {

            // prepare data
            RequestConfiguration configuration = RequestConfigurationService.getInstance().build(notificationRequest);

            // get notification data
            String notificationMessage = PluginUtils.inputStreamToString(notificationRequest.getContent());

            Map<String, String> mNotificationMessage = jsonService.xmlToMap(notificationMessage);

            // verify Signature
            String key = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.KEY);
            SignType signType = SignType.valueOf(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE));
            if (!signatureUtil.isSignatureValid(mNotificationMessage, key, signType)) {
                log.error("Invalid sign value in XML: {}", notificationMessage);
                throw new PluginException("Invalid signature", FailureCause.INVALID_DATA);
            }

            NotificationMessage message = jsonService.mapToObject(mNotificationMessage, NotificationMessage.class);
            String transactionId = message.getTransactionId();

            // call WeChatPay API to get the transaction status
            QueryOrderRequest queryOrderRequest = QueryOrderRequest.builder()
                    .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                    .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                    .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                    .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                    .deviceInfo(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.DEVICE_INFO))
                    .transactionId(transactionId)
                    .nonceStr(PluginUtils.generateRandomString(32))
                    .signType(SignType.valueOf(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                    .build();

            QueryOrderResponse queryOrderResponse = httpService.queryOrder(configuration, queryOrderRequest);

            // check transaction status
            TradeState tradeState = queryOrderResponse.getTradeState();
            partnerTransactionId = queryOrderResponse.getTransactionId();
            BuyerPaymentId buyerPaymentId = new EmptyTransactionDetails();

            switch (tradeState) {
                case SUCCESS:
                    paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                            .aPaymentResponseSuccess()
                            .withPartnerTransactionId(partnerTransactionId)
                            .withStatusCode(tradeState.name())
                            .withTransactionDetails(buyerPaymentId)
                            .build();
                    break;
                case NOTPAY:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withPartnerTransactionId(partnerTransactionId)
                            .withErrorCode(queryOrderResponse.getErrorCode())
                            .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR) // todo c'est quoi qui va la?
                            .withTransactionDetails(buyerPaymentId)
                            .build();
                    break;
                default:
                    return new IgnoreNotificationResponse();    // todo
            }

        } catch (PluginException e) {
            log.info("a PluginException occurred", e);
            paymentResponse = e.toPaymentResponseFailureBuilder()
                    .withPartnerTransactionId(partnerTransactionId)
                    .build();

        } catch (RuntimeException e) {
            log.error("Unexpected plugin error", e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginUtils.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

        String httpBody = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";   // todo on laisse ca en dur ou on bidouille?
        TransactionCorrelationId correlationId = TransactionCorrelationId.TransactionCorrelationIdBuilder
                .aCorrelationIdBuilder()
                .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                .withValue(partnerTransactionId)
                .build();

        notificationResponse = PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder
                .aPaymentResponseByNotificationResponseBuilder()
                .withPaymentResponse(paymentResponse)
                .withHttpBody(httpBody)
                .withHttpStatus(200)
                .withTransactionCorrelationId(correlationId)
                .build();

        return notificationResponse;
    }

    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // does nothing
    }
}
