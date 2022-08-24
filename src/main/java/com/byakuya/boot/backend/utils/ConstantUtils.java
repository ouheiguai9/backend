package com.byakuya.boot.backend.utils;

/**
 * Created by ganzl on 2020/4/2.
 * 常量工具类,用于定义系统使用的所有常量
 */
public class ConstantUtils {
    //附加资源路径
    public static final String ADDITIONAL_RESOURCE_PATH = "/additional/";
    //认证类型
    public static final String AUTH_TYPE_KEY = "_auth_type_key_";
    //授权页面token键值
    public static final String AUTH_PAGE_TOKEN_KEY = "_auth_page_token_";
    //安全REST请求前缀
    public static final String AUTH_REST_API_PREFIX = "/auth/api";
    //普通REST请求前缀
    public static final String REST_API_PREFIX = "/api";
    //验证码在请求中的参数名
    public static final String CAPTCHA_PARAMETER_KEY = "_captcha_";
    //header中对异常信息的返回属性参数名
    public static final String HEADER_ERROR_ATTRIBUTE_KEY = "Header-Error-Attribute";
    //默认对外REST请求前缀
    public static final String OPEN_REST_API_PREFIX = "/open/api";
    //验证码在session中的键名
    public static final String SESSION_CAPTCHA_KEY = "_session_captcha_";
    //文件上传路径
    public static final String UPLOAD_DIR = "./upload";

    private ConstantUtils() {

    }
}
