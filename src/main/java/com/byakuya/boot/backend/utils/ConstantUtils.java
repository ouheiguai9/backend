package com.byakuya.boot.backend.utils;

/**
 * Created by 田伯光 at 2022/10/12 23:56
 * 常量工具类,用于定义系统使用的所有常量
 */
public final class ConstantUtils {
    //认证类型
    public static final String AUTH_TYPE_KEY = "_auth_type_key_";
    //对外REST API请求前缀
    public static final String OPEN_API_PREFIX = "/api";
    //header中token存放键值
    public static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";
    //默认
    public static final String DEFAULT_ERROR_PATH = "${server.error.path:${error.path:/error}}";
    //最大登录错误次数
    public static final int LOGIN_ERROR_LIMIT_COUNT = 5;
    //登录错误此时等待分钟数
    public static final long LOGIN_ERROR_WAIT_MINUTES = 15;

    private ConstantUtils() {

    }
}
