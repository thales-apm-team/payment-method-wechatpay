import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.request.DownloadTransactionHistoryRequest;
import com.payline.payment.wechatpay.bean.request.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.response.Response;
import com.payline.payment.wechatpay.bean.response.UnifiedOrderResponse;
import com.payline.payment.wechatpay.service.HttpService;
import com.payline.payment.wechatpay.service.QRCodeService;
import com.payline.payment.wechatpay.util.PluginUtils;
import com.payline.payment.wechatpay.util.constant.ContractConfigurationKeys;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static String keyMD5 = "2ab9071b06b9f739b950ddb41db2690d";
    static String keySHA256 = "5df05d27ce49d87ca38f046325ea3c4d";

    static String appId = "wxa5b511bc130a4d9e";

    static String merchantId = "110605603";
    static String subMerchantId = "345923236";

    static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "https://www.redirection.url.com",
                "http://redirectionCancelURL.com",
                true);
    }

    static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(PartnerConfigurationKeys.UNIFIED_ORDER_URL, "https://api.mch.weixin.qq.com/pay/unifiedorder");
        partnerConfigurationMap.put(PartnerConfigurationKeys.DOWNLOAD_TRANSACTIONS_URL, "https://api.mch.weixin.qq.com/pay/downloadbill");
        partnerConfigurationMap.put(PartnerConfigurationKeys.SIGN_TYPE, "MD5");
        partnerConfigurationMap.put(PartnerConfigurationKeys.KEY, keyMD5);
        partnerConfigurationMap.put(PartnerConfigurationKeys.APPID, appId);
        partnerConfigurationMap.put(PartnerConfigurationKeys.DEVICE_INFO, "WEB");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();

        contractProperties.put(ContractConfigurationKeys.MERCHANT_ID, new ContractProperty(merchantId));
        contractProperties.put(ContractConfigurationKeys.SUB_MERCHANT_ID, new ContractProperty(subMerchantId));
        return new ContractConfiguration("wechatPay", contractProperties);
    }

    public static void main(String[] arg) {
        HttpService httpService = HttpService.getInstance();
        QRCodeService qrCodeService = QRCodeService.getInstance();
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
                    .subMerchantId("345923236")
                    .nonceStr("123456")
                    .signType(SignType.MD5)
                    .build();

            UnifiedOrderResponse response = httpService.unifiedOrder(requestConfiguration, request);

            System.out.println(response.getCodeUrl());

            BufferedImage image = qrCodeService.generateMatrix(response.getCodeUrl(), 300);
            File file = new File("/home/dev/Documents/qrcode");
            ImageIO.write(image, "png", file);

            DownloadTransactionHistoryRequest downloadTransactionHistoryRequest = DownloadTransactionHistoryRequest.builder()
                    .appId(requestConfiguration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.APPID))
                    .merchantId(requestConfiguration.getContractConfiguration().getProperty(ContractConfigurationKeys.MERCHANT_ID).getValue())
                    .subAppId(requestConfiguration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SUB_APPID))
                    .subMerchantId(requestConfiguration.getContractConfiguration().getProperty(ContractConfigurationKeys.SUB_MERCHANT_ID).getValue())
                    .nonceStr(PluginUtils.generateRandomString(32))
                    .signType(SignType.valueOf(requestConfiguration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.SIGN_TYPE)))
                    .billDate(PluginUtils.createDate())
                    .billType("ALL")
                    .build();

            Response response2 = httpService.DownloadTransactionHistory(requestConfiguration, downloadTransactionHistoryRequest);

            System.out.println(response2.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
