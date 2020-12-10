package com.payline.payment.wechatpay.service;

import com.github.wxpay.sdk.WXPayConstants;
import com.payline.payment.wechatpay.bean.request.QueryOrderRequest;
import com.payline.payment.wechatpay.bean.response.QueryOrderResponse;
import com.payline.payment.wechatpay.bean.response.Response;
import com.payline.payment.wechatpay.bean.request.SubmitRefundRequest;
import com.payline.payment.wechatpay.bean.request.UnifiedOrderRequest;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.bean.nested.Code;
import com.payline.payment.wechatpay.bean.nested.SignType;
import com.payline.payment.wechatpay.bean.response.SubmitRefundResponse;
import com.payline.payment.wechatpay.bean.response.UnifiedOrderResponse;
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


    protected Header[] initHeaders() {
        return new Header[]{
                new BasicHeader("Content-Type", "text/xml")
        };
    }

    public UnifiedOrderResponse unifiedOrder(RequestConfiguration configuration, UnifiedOrderRequest request) {
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
            UnifiedOrderResponse unifiedOrderResponse = jsonService.mapToObject(jsonService.xmlToMap(response.getContent()), UnifiedOrderResponse.class);


            // check response
            checkResponse(unifiedOrderResponse, key,request.getSignType());

            return unifiedOrderResponse;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public QueryOrderResponse queryOrder(RequestConfiguration configuration, QueryOrderRequest request) {
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
            QueryOrderResponse queryOrderResponse = JsonService.getInstance().fromJson(response.getContent(), QueryOrderResponse.class);

            // check response
            checkResponse(queryOrderResponse, key,request.getSignType());

            return queryOrderResponse;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SubmitRefundResponse submitRefund(RequestConfiguration configuration, SubmitRefundRequest request) {
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
            SubmitRefundResponse submitRefundResponse = JsonService.getInstance().fromJson(response.getContent(), SubmitRefundResponse.class);

            // check response
            checkResponse(submitRefundResponse, key,request.getSignType());

            return submitRefundResponse;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkResponse(Response response, String key, SignType signType) {
        Code returnCode = response.getReturnCode();
        if (returnCode.equals(Code.FAIL)) {
            log.error("return_code FAIL in response {}", response.toString());
            throw new PluginException(response.getReturnMessage(), FailureCause.PAYMENT_PARTNER_ERROR);
        } else {
            // verify Signature
            Map<String,String> respData = jsonService.objectToMap(response);
            if (SignatureUtil.isSignatureValid(respData, key, signType)) {
                // check resultCode
                Code resultCode = response.getResultCode();
                if (resultCode.equals(Code.FAIL)) {
                    log.error("result_code FAIL in response {}", response.toString());
                    throw new PluginException(response.getErrorCodeDescription(), FailureCause.INVALID_DATA); // todo f(error_code)
                }
            } else {
                log.error("Invalid sign value in XML: {}", response.toString());
                throw new PluginException("Invalid signature", FailureCause.INVALID_DATA);
            }
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
