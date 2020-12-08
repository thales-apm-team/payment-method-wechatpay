package com.payline.payment.wechatpay.util;

import com.github.wxpay.sdk.CustomConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.payline.payment.wechatpay.bean.Response;
import com.payline.payment.wechatpay.bean.Request;
import com.payline.payment.wechatpay.bean.configuration.RequestConfiguration;
import com.payline.payment.wechatpay.exception.PluginException;
import com.payline.pmapi.bean.common.FailureCause;

import java.util.Map;

public class ClientService {
    CustomConfig customConfig;
    WXPay wxPay;
    JsonService converter;


    private static class Holder {
        private static final ClientService instance = new ClientService();
    }

    public static ClientService getInstance() {
        return ClientService.Holder.instance;
    }


    public void init(RequestConfiguration configuration){
        converter = JsonService.getInstance();
        try {
            CustomConfig customConfig = new CustomConfig(configuration);
            wxPay = new WXPay(customConfig, configuration.getEnvironment().getNotificationURL(), configuration.getEnvironment().isSandbox());
        } catch (Exception e) {
            e.printStackTrace();
            // todo
        }
    }


    /**
     * Call for getting refund info
     * @param request all info needed to get a refund
     * @return all info about the refund
     */
    public Response testConnection(Request request){
        try{
            Map<String, String> data = converter.objectToMap(request);
            Map<String, String> responseMap = wxPay.report(data);
            Response response = converter.mapToObject(responseMap, Response.class);

            checkResponse(response);
            return response;
        }catch (Exception e) {
            e.printStackTrace();
            // todo

            return null;
        }
    }

    /**
     * Call for creating a transaction
     * @param request all info needed to create a transaction
     * @return all info about the created transaction
     */
    public Response createTransaction(Request request){
        try {
            Map<String, String> data = converter.objectToMap(request);
            Map<String, String> responseMap = wxPay.unifiedOrder(data);
            Response response = converter.mapToObject(responseMap, Response.class);

            checkResponse(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            // todo

            return null;
        }
    }

    /**
     * Call for getting a transaction info
     * @param request all info needed to get a transaction
     * @return all info about the transaction
     */
    public Response getTransaction(Request request){
        try{
            Map<String, String> data = converter.objectToMap(request);
            Map<String, String> responseMap = wxPay.orderQuery(data);
            Response response = converter.mapToObject(responseMap, Response.class);

            checkResponse(response);
            return response;
        }catch (Exception e) {
            e.printStackTrace();
            // todo

            return null;
        }
    }

    /**
     * Call for creating a refund
     * @param request all info needed to create a refund
     * @return all info about the refund
     */
    public Response createRefund(Request request){
        try{
            Map<String, String> data = converter.objectToMap(request);
            Map<String, String> responseMap = wxPay.refund(data);
            Response response = converter.mapToObject(responseMap, Response.class);

            checkResponse(response);
            return response;
        }catch (Exception e) {
            e.printStackTrace();
            // todo

            return null;
        }
    }

    /**
     * Call for getting refund info
     * @param request all info needed to get a refund
     * @return all info about the refund
     */
    public Response getRefund(Request request){
        try{
            Map<String, String> data = converter.objectToMap(request);
            Map<String, String> responseMap = wxPay.refundQuery(data);
            Response response = converter.mapToObject(responseMap, Response.class);

            checkResponse(response);
            return response;
        }catch (Exception e) {
            e.printStackTrace();
            // todo

            return null;
        }
    }



    /**
     * check if Response is Ok by checking its returnCode and resultCode
     * @param response the response to verify
     */
    private void checkResponse(Response response){
        if (!response.getReturnCode().equals("SUCCESS")){
            throw new PluginException(response.getReturnMessage(), FailureCause.PAYMENT_PARTNER_ERROR);
        }else if (!response.getResultCode().equals("SUCCESS")){
            throw new PluginException(response.getErrorCodeDescription(), FailureCause.INVALID_DATA); // todo un errorMapper
        }
    }





}
