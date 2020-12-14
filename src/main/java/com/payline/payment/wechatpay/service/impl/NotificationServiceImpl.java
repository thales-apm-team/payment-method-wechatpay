package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.nested.TradeState;
import com.payline.payment.wechatpay.bean.request.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.response.NotificationMessage;
import com.payline.payment.wechatpay.bean.response.QueryOrderResponse;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.JsonService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BuyerPaymentId;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {
    JsonService jsonService = JsonService.getInstance();
    HttpService httpService = HttpService.getInstance();

    @Override
    public NotificationResponse parse(NotificationRequest notificationRequest) {
        // prepare data
        RequestConfiguration configuration = RequestConfigurationService.getInstance().build(notificationRequest);
        PaymentResponse paymentResponse;
        NotificationResponse notificationResponse;

        // get notification data
        String notificationMessage = PluginUtils.inputStreamToString(notificationRequest.getContent());
        NotificationMessage message = jsonService.mapToObject(jsonService.xmlToMap(notificationMessage), NotificationMessage.class);
        String transactionId = message.getTransactionId();

        // call WeChatPay API to get the transaction status
        QueryOrderRequest queryOrderRequest = QueryOrderRequest.builder()
                .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                .deviceInfo("WEB")
                .transactionId(transactionId)
                .nonceStr(PluginUtils.generateRandomString(32))
                .signType(SignType.valueOf( configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                .build();

        QueryOrderResponse queryOrderResponse = httpService.queryOrder(configuration, queryOrderRequest);

        // check transaction status
        TradeState tradeState = queryOrderResponse.getTradeState();
        String partnerTransactionId = queryOrderResponse.getTransactionId();
        BuyerPaymentId buyerPaymentId = new EmptyTransactionDetails();

        if (tradeState.equals(TradeState.SUCCESS)) {
            paymentResponse = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                    .aPaymentResponseSuccess()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withStatusCode(tradeState.name())
                    .withTransactionDetails(buyerPaymentId)
                    .build();
        } else {
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(queryOrderResponse.getErrorCode())
                    .withFailureCause(FailureCause.PARTNER_UNKNOWN_ERROR) // todo c'est quoi qui va la?
                    .withTransactionDetails(buyerPaymentId)
                    .build();
        }

            String httpBody = "";   // todo retrouver ce qu'on doit répondre
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
        public void notifyTransactionStatus (NotifyTransactionStatusRequest notifyTransactionStatusRequest){
            // does nothing
        }
    }
