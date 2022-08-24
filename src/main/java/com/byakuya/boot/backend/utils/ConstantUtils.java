package com.byakuya.boot.backend.utils;

/**
 * Created by ganzl on 2020/4/2.
 * 常量工具类,用于定义系统使用的所有常量
 */
public final class ConstantUtils {
    //认证类型
    public static final String AUTH_TYPE_KEY = "_auth_type_key_";
    //普通REST请求前缀
    public static final String REST_API_PREFIX = "/api";
    //header中对异常信息的返回属性参数名
    public static final String HEADER_ERROR_ATTRIBUTE_KEY = "Header-Error-Attribute";
    //header中token存放键值
    public static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

    private ConstantUtils() {

    }
}
