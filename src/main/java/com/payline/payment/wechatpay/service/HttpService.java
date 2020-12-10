package com.payline.payment.wechatpay.service;

import com.github.wxpay.sdk.WXPayConstants;
import com.payline.payment.wechatpay.bean.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.Response;
import com.payline.payment.wechatpay.bean.SubmitRefundRequest;
import com.payline.payment.wechatpay.bean.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.exception.InvalidDataException;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.payment.wechatpay.util.JsonService;
import com.payline.payment.wechatpay.util.constant.PartnerConfigurationKeys;
import com.payline.payment.wechatpay.util.http.HttpClient;
import com.payline.payment.wechatpay.util.http.StringResponse;
import com.payline.payment.wechatpay.util.security.SignatureUtil;
import com.payline.pmapi.bean.common.FailureCause;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Log4j2
public class HttpService {
    private HttpClient client = HttpClient.getInstance();
    private JsonService jsonService = JsonService.getInstance();

    private HttpService() {
    }

    private static class Holder {
        private static final HttpService instance = new HttpService();
    }


    public static HttpService getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END


    protected Header[] initHeaders() {  // todo voir si on met ca en static on directement dans l'appel doPost
        return new Header[]{
                new BasicHeader("Content-Type", "text/xml")
        };
    }

    public Response unifiedOrder(RequestConfiguration configuration, UnifiedOrderRequest request) {
        try {
            // get needed data
            String key = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.KEY);

            URI uri = new URI(configuration
                    .getPartnerConfiguration()
                    .getProperty(PartnerConfigurationKeys.UNIFIED_ORDER_URL)
            );
            Header[] headers = initHeaders();
            Map<String, String> map = jsonService.objectToMap(request);
            String body = SignatureUtil.generateSignedXml(map, key, request.getSignType());

            // does the call
            StringResponse response = client.post(uri, headers, body);


            // check response
            checkResponse(response, key,request.getSignType());

            return jsonService.mapToObject(jsonService.xmlToMap(response.getContent()), Response.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response queryOrder(RequestConfiguration configuration, QueryOrderRequest request) {
        try {
            // get needed data
            String key = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.KEY);

            URI uri = new URI(configuration
                    .getPartnerConfiguration()
                    .getProperty(PartnerConfigurationKeys.QUERY_ORDER_URL)
            );
            Header[] headers = initHeaders();
            Map<String, String> map = jsonService.objectToMap(request);
            String body = SignatureUtil.generateSignedXml(map, key, request.getSignType());

            // does the call
            StringResponse response = client.post(uri, headers, body);

            // check response
            checkResponse(response, key,request.getSignType());

            return JsonService.getInstance().fromJson(response.getContent(), Response.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response submitRefund(RequestConfiguration configuration, SubmitRefundRequest request) {
        try {
            // get needed data
            String key = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.KEY);

            URI uri = new URI(configuration
                    .getPartnerConfiguration()
                    .getProperty(PartnerConfigurationKeys.SUBMIT_REFUND_URL)
            );
            Header[] headers = initHeaders();
            Map<String, String> map = jsonService.objectToMap(request);
            String body = SignatureUtil.generateSignedXml(map, key, request.getSignType());

            // does the call
            StringResponse response = client.post(uri, headers, body);

            // check response
            checkResponse(response, key,request.getSignType());

            return JsonService.getInstance().fromJson(response.getContent(), Response.class);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkResponse(StringResponse response, String key, SignType signType) {
//        if (!response.isSuccess())// todo trh error
        String bodyResponse = response.getContent();

        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = jsonService.xmlToMap(bodyResponse);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        } else {
            log.error("No `return_code` in XML: %s", bodyResponse);
            throw new PluginException("No return_code");
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            log.error("return_code FAIL"); // todo meilleur message
            throw new PluginException(respData.get("return_msg"), FailureCause.PAYMENT_PARTNER_ERROR);
        } else if (return_code.equals(WXPayConstants.SUCCESS)) {
            // verify Signature
            if (SignatureUtil.isSignatureValid(respData, key, signType)) {
                // check resultCode
                String resultCode = respData.get("result_code");
                if (resultCode == null) {
                    log.error("No `result_code` in XML: %s", bodyResponse);
                    throw new PluginException("No return_code", FailureCause.INVALID_DATA);
                } else if (resultCode.equals("FAIL")) {
                    log.error(""); // todo faire mieux;
                    throw new PluginException(respData.get("error_code_des"), FailureCause.INVALID_DATA); // todo f(error_code)
                } else if (!resultCode.equals("SUCCESS")) {
                    throw new PluginException("unknown result_code", FailureCause.INVALID_DATA); // todo f(error_code)
                }
            } else {
                log.error("Invalid sign value in XML: %s", bodyResponse);
                throw new PluginException("Invalid signature", FailureCause.INVALID_DATA);
            }
        } else {
            log.error("return_code value %s is invalid in XML: %s", return_code, bodyResponse);
            throw new PluginException("invalid return_code", FailureCause.INVALID_DATA);
        }
    }


    /**
     * Check if the datas required for all request are empty or not
     *
     * @param configuration the request configuration
     */
    private void verifyData(RequestConfiguration configuration) {
        if (configuration.getPartnerConfiguration() == null) {
            throw new InvalidDataException("PartnerConfiguration is empty");
        }

        if (configuration.getContractConfiguration() == null) {
            throw new InvalidDataException("ContractConfiguration is empty");
        }

        // todo ajouter les verif d'url, d'id etc...
    }


}
