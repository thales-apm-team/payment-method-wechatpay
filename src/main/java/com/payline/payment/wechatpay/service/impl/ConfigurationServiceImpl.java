package com.payline.payment.wechatpay.service.impl;

import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.service.ConfigurationService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigurationServiceImpl implements ConfigurationService {
    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        return null;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        return null;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return null;
    }

    @Override
    public String getName(Locale locale) {
        return null;
    }
}
