package com.byakuya.boot.backend.component.dfb;

/**
 * Created by 田伯光 at 2023/2/16 13:56
 * 常量工具类
 */
public final class ConstantUtils {
    public static final String REDIS_PREFIX = "dfb:";
    public static final String LAWYER_PREFIX = REDIS_PREFIX + "lawyer:";
    public static final String CANDIDATES_KEY = LAWYER_PREFIX + "candidates";
    public static final String BACKUP_KEY = LAWYER_PREFIX + "backups";
    public static final String LOCKED_LAWYER_PREFIX_KEY = LAWYER_PREFIX + "locked:";
    public static final String CUSTOMER_PREFIX = REDIS_PREFIX + "customer:";
    public static final String INVALID_CUSTOMER_PREFIX = CUSTOMER_PREFIX + "invalid:";

    private ConstantUtils() {

    }
}
