package com.payline.payment.wechatpay.util;

import java.util.Map;

public class Converter {
    JsonService json = JsonService.getInstance();
    XMLService xml = XMLService.getInstance();

    private static class Holder {
        private static final Converter instance = new Converter();
    }

    public static Converter getInstance() {
        return Converter.Holder.instance;
    }


    /**
     * convert a bean to a map
     * @param o the object to convert
     * @return a map with field name as key and fields value as value
     */
    public Map<String, String> objectToMap(Object o) {
        return json.fromJson(json.toJson(o), Map.class);
    }


    /**
     * convert a Map into a bean
     * @param map the map to convert
     * @param clazz the bean class to create
     * @param <T>
     * @return
     */
    public <T> T mapToObject(Map<String, String> map, Class<T> clazz) {
        return json.fromJson(json.toJson(map), clazz);
    }

    /**
     * Convert an XML String
     * @param s the XML String to convert
     * @param clazz the bean class
     * @param <T>
     * @return
     */
    public <T> T xmlToObject(String s, Class<T> clazz) {
        return mapToObject(xml.xmlToMap(s), clazz);
    }


}
