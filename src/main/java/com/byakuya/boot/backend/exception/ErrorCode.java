package com.byakuya.boot.backend.exception;

import org.springframework.lang.Nullable;

/**
 * Created by 田伯光 at 2022/8/22 23:06
 */
public enum ErrorCode {
    CODE_UNKNOWN(0, "未知异常"),
    DB_RECORD_NOT_FOUND(20001, "记录不存在"),
    AUTHENTICATION_FAIL(40001, "认证失败"),
    AUTHENTICATION_NOT_FOUND(40002, "账户不存在"),
    AUTHENTICATION_ERROR_PASS(40003, "密码错误"),
    AUTHENTICATION_DISABLE(40004, "账户被锁定"),
    AUTHENTICATION_ERROR_LIMIT(40005, "错误次数过多");
    public final int value;
    public final Series series;
    public final String reason;

    ErrorCode(int value, String reason) {
        this.value = value;
        this.series = Series.valueOf(value);
        this.reason = reason;
    }


    public enum Series {
        CODE(0, "代码异常"),
        DB(2, "数据库异常"),
        AUTHENTICATION(4, "认证异常"),
        BUSINESS(9, "业务异常");
        public final int value;
        public final String name;

        Series(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static Series valueOf(int errorCode) {
            Series series = resolve(errorCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + errorCode + "]");
            }
            return series;
        }

        @Nullable
        public static Series resolve(int statusCode) {
            int seriesCode = statusCode / 10000;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            return null;
        }
    }
}
