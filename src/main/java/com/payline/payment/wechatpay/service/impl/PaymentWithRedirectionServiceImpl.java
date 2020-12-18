package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.nested.TradeState;
import com.payline.payment.wechatpay.bean.request.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.response.QueryOrderResponse;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.BuyerPaymentId;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    HttpService httpService = HttpService.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        // todo on peut return null ici?
        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        PaymentResponse paymentResponse;
        try {

            RequestConfiguration configuration = RequestConfigurationService.getInstance().build(transactionStatusRequest);

            QueryOrderRequest queryOrderRequest = QueryOrderRequest.builder()
                    .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                    .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                    .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                    .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                    .deviceInfo(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.DEVICE_INFO))
                    .transactionId(transactionStatusRequest.getTransactionId()) // todo verifier ca
                    .nonceStr(PluginUtils.generateRandomString(32))
                    .signType(SignType.valueOf(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                    .build();

            QueryOrderResponse response = httpService.queryOrder(configuration, queryOrderRequest);

            String partnerTransactionId = response.getTransactionId();
            BuyerPaymentId buyerPaymentId = new EmptyTransactionDetails(); // TODO: À vérifier
            TradeState tradeState = response.getTradeState();
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
                            .withErrorCode(response.getErrorCode())
                            .withFailureCause(FailureCause.PAYMENT_PARTNER_ERROR)
                            .withTransactionDetails(buyerPaymentId)
                            .build();
                    break;
                default:
                    paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                            .aPaymentResponseFailure()
                            .withPartnerTransactionId(partnerTransactionId)
                            .withErrorCode(response.getErrorCode())
                            .withFailureCause(FailureCause.INVALID_DATA) // todo ca ou un autre mais a definir
                            .withTransactionDetails(buyerPaymentId)
                            .build();
            }
        } catch (PluginException e) {
            log.info("a PluginException occurred", e);
            paymentResponse = e.toPaymentResponseFailureBuilder().build();

        } catch (RuntimeException e) {
            log.error("Unexpected plugin error", e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

        return paymentResponse;
    }
}
