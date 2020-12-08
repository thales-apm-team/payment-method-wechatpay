import com.github.wxpay.sdk.CustomConfig;
import com.github.wxpay.sdk.WXPay;
import com.payline.payment.wechatpay.bean.Response;
import com.payline.payment.wechatpay.bean.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.util.JsonService;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;

import java.util.HashMap;
import java.util.Map;

public class Main {
    static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "https://www.redirection.url.com",
                "http://redirectionCancelURL.com",
                true);
    }

    static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(PartnerConfigurationKeys.UNIFIED_ORDER_URL, "https://apihk.mch.weixin.qq.com/pay/unifiedorder");
        partnerConfigurationMap.put(PartnerConfigurationKeys.KEY, "5df05d27ce49d87ca38f046325ea3c4d");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();

        contractProperties.put(ContractConfigurationKeys.MERCHANT_ID, new ContractProperty("123123"));
        return new ContractConfiguration("wechatPay", contractProperties);
    }

    public static void main(String[] arg) {
        HttpService httpService = HttpService.getInstance();

        try {
            RequestConfiguration requestConfiguration = new RequestConfiguration(
                    aContractConfiguration(),
                    anEnvironment(),
                    aPartnerConfiguration());

            UnifiedOrderRequest request = UnifiedOrderRequest.builder()
                    .body("salut")
                    .outTradeNo("2016090910595900000012")
                    .deviceInfo("")
                    .feeType("CNY")
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

            httpService.unifiedOrder(requestConfiguration, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}