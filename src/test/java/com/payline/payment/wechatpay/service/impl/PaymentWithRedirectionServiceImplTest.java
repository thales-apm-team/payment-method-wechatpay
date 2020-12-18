package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.MockUtils;
import com.payline.payment.wechatpay.bean.nested.Code;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.nested.TradeState;
import com.payline.payment.wechatpay.bean.response.QueryOrderResponse;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

class PaymentWithRedirectionServiceImplTest {

    @InjectMocks
    PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();
    @Mock
    private HttpService httpService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void handleSessionExpired_SUCCESS(){

        QueryOrderResponse queryOrderResponse =  QueryOrderResponse.builder()
                .returnCode(Code.SUCCESS)
                .appId("appId")
                .merchantId("merchantId")
                .errorCode("errorCode")
                .nonceStr("nonceStr")
                .errorCodeDescription("errorCodeDescription")
                .resultCode(Code.SUCCESS)
                .returnMessage("returnMessage")
                .sign("sign")
                .signType(SignType.HMACSHA256)
                .subAppId("subAppId")
                .subMerchantId("subMerchantId")
                .tradeState(TradeState.SUCCESS)
                .transactionId("transactionId")
                .build();

        Mockito.doReturn(queryOrderResponse).when(httpService).queryOrder(any(),any());

        PaymentResponse response =  service.handleSessionExpired(MockUtils.aPaylineTransactionStatusRequest());

        Assertions.assertEquals(PaymentResponseSuccess.class, response.getClass());
        Assertions.assertEquals(queryOrderResponse.getTransactionId(), ((PaymentResponseSuccess) response).getPartnerTransactionId());
        Assertions.assertEquals(queryOrderResponse.getTradeState().name(), ((PaymentResponseSuccess) response).getStatusCode());

    }
    @Test
    void handleSessionExpired_NOTPAY(){

        QueryOrderResponse queryOrderResponse =  QueryOrderResponse.builder()
                .returnCode(Code.SUCCESS)
                .appId("appId")
                .merchantId("merchantId")
                .errorCode("errorCode")
                .nonceStr("nonceStr")
                .errorCodeDescription("errorCodeDescription")
                .resultCode(Code.SUCCESS)
                .returnMessage("returnMessage")
                .sign("sign")
                .signType(SignType.HMACSHA256)
                .subAppId("subAppId")
                .subMerchantId("subMerchantId")
                .tradeState(TradeState.NOTPAY)
                .transactionId("transactionId")
                .build();

        Mockito.doReturn(queryOrderResponse).when(httpService).queryOrder(any(),any());

        PaymentResponse response =  service.handleSessionExpired(MockUtils.aPaylineTransactionStatusRequest());


        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        Assertions.assertEquals(queryOrderResponse.getTransactionId(), ((PaymentResponseFailure) response).getPartnerTransactionId());
        Assertions.assertEquals(queryOrderResponse.getErrorCode(), ((PaymentResponseFailure) response).getErrorCode());
        Assertions.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, ((PaymentResponseFailure) response).getFailureCause());

    }

    @Test
    void handleSessionExpired_INVALID_DATA(){

        QueryOrderResponse queryOrderResponse =  QueryOrderResponse.builder()
                .returnCode(Code.SUCCESS)
                .appId("appId")
                .merchantId("merchantId")
                .errorCode("errorCode")
                .nonceStr("nonceStr")
                .errorCodeDescription("errorCodeDescription")
                .resultCode(Code.SUCCESS)
                .returnMessage("returnMessage")
                .sign("sign")
                .signType(SignType.HMACSHA256)
                .subAppId("subAppId")
                .subMerchantId("subMerchantId")
                .tradeState(TradeState.REFUND)
                .transactionId("transactionId")
                .build();

        Mockito.doReturn(queryOrderResponse).when(httpService).queryOrder(any(),any());

        PaymentResponse response =  service.handleSessionExpired(MockUtils.aPaylineTransactionStatusRequest());


        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        Assertions.assertEquals(queryOrderResponse.getTransactionId(), ((PaymentResponseFailure) response).getPartnerTransactionId());
        Assertions.assertEquals(queryOrderResponse.getErrorCode(), ((PaymentResponseFailure) response).getErrorCode());
        Assertions.assertEquals(FailureCause.INVALID_DATA, ((PaymentResponseFailure) response).getFailureCause());

    }

    @Test
    void paymentRequest_PluginException() {
        Mockito.doThrow(new PluginException("foo")).when(httpService).queryOrder(any(), any());

        // when: sending the request, a PluginException is thrown
        PaymentResponse paymentResponse = service.handleSessionExpired(MockUtils.aPaylineTransactionStatusRequest());

        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponse.getClass());
        Assertions.assertEquals(FailureCause.INTERNAL_ERROR, ((PaymentResponseFailure) paymentResponse).getFailureCause());
    }

    @Test
    void paymentRequest_RunTimeException() {
        Mockito.doThrow(new RuntimeException("foo")).when(httpService).queryOrder(any(), any());

        // when: sending the request, a PluginException is thrown
        PaymentResponse paymentResponse = service.handleSessionExpired(MockUtils.aPaylineTransactionStatusRequest());

        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponse.getClass());
        Assertions.assertEquals(FailureCause.INTERNAL_ERROR, ((PaymentResponseFailure) paymentResponse).getFailureCause());
    }
}
