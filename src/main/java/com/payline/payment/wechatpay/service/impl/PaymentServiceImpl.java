package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.bean.response.Response;
import com.payline.payment.wechatpay.bean.request.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.response.UnifiedOrderResponse;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.QRCodeService;
import com.payline.payment.wechatpay.service.RequestConfigurationService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.service.PaymentService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PaymentServiceImpl implements PaymentService {
    private RequestConfigurationService requestConfigurationService = RequestConfigurationService.getInstance();
    private HttpService httpService = HttpService.getInstance();
    private QRCodeService qrCodeService = QRCodeService.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse;
        try{
            RequestConfiguration configuration = requestConfigurationService.build(paymentRequest);

            // create request object
            UnifiedOrderRequest request = UnifiedOrderRequest.builder()
                    .body(paymentRequest.getSoftDescriptor())
                    .outTradeNo(paymentRequest.getTransactionId())
                    .deviceInfo(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.DEVICE_INFO))
                    .feeType(paymentRequest.getAmount().getCurrency().getCurrencyCode())
                    .totalFee(paymentRequest.getAmount().getAmountInSmallestUnit().toString())
                    .spBillCreateIp(paymentRequest.getBrowser().getIp())
                    .notifyUrl(configuration.getEnvironment().getNotificationURL())
                    .tradeType("NATIVE")
                    .productId(paymentRequest.getOrder().getReference())
                    .appId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                    .merchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                    .subAppId(configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                    .subMerchantId(configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                    .nonceStr(PluginUtils.generateRandomString(32))
                    .signType(SignType.valueOf( configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                    .build();

            // call WeChatPay API
            UnifiedOrderResponse unifiedOrderResponse = httpService.unifiedOrder(configuration, request);
            String qrCode = unifiedOrderResponse.getCodeUrl();


            // return QRCode
            qrCodeService.generateMatrix(qrCode, 300); // todo 300?
            paymentResponse = null; // todo ici faire de la magie pour retourner un QRCode



        }catch (PluginException e){
            log.info("a PluginException occurred", e);
            paymentResponse = e.toPaymentResponseFailureBuilder().build();

        }catch (RuntimeException e){
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
