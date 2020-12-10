package com.payline.payment.wechatpay.service.impl;

import com.payline.payment.wechatpay.service.LogoPaymentFormConfigurationService;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        final NoFieldForm noFieldForm = NoFieldForm
                .NoFieldFormBuilder
                .aNoFieldForm()
                .withDisplayButton(true)
                .withButtonText("") // todo
                .withDescription("")    // todo
                .build();

        return PaymentFormConfigurationResponseSpecific
                .PaymentFormConfigurationResponseSpecificBuilder
                .aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }
}
