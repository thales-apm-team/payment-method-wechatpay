package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.Response;
import com.payline.payment.wechatpay.bean.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    private RequestConfigurationService requestConfigurationService = RequestConfigurationService.getInstance();
    private HttpService httpService = HttpService.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse;
        try{
            RequestConfiguration configuration = requestConfigurationService.build(paymentRequest);

            // create request object
            UnifiedOrderRequest request = UnifiedOrderRequest.builder()
                    .body(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                    .outTradeNo(paymentRequest.getTransactionId())
                    .deviceInfo("WEB")
                    .feeType(paymentRequest.getAmount().getCurrency().getCurrencyCode())
                    .totalFee("1")
                    .spBillCreateIp("123.12.12.123")
                    .notifyUrl("https://webhook.site/3d9e37dd-725b-4e61-a910-f1cdfa1ec78f")
                    .tradeType("NATIVE")
                    .productId("12")
                    .appId("wxa5b511bc130a4d9e")
                    .merchantId("110605603")
                    .subAppId("1")
                    .subMerchantId("0")
                    .nonceStr("123456")
                    .signType("HMAC-SHA256")
                    .build();

            // call WeChatPay API
            Response response = httpService.unifiedOrder(configuration, request);


            // return QRCode
            paymentResponse = null; // todo ici faire de la magie pour retourner un QRCode



        }catch (PluginException e){
            paymentResponse = e.toPaymentResponseFailureBuilder().build();

        }catch (RuntimeException e){
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
        return paymentResponse;
    }
}
