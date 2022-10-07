package com.byakuya.boot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * Created by 田伯光 at 2022/8/22 23:06
 */
public enum ErrorStatus {
    CODE_UNKNOWN(0, "未知错误"),
    DB_CONSTRAINT_VIOLATION(20005, "违反数据库约束"),
    DB_RECORD_NOT_FOUND(21001, "记录不存在"),
    DB_RECORD_DUPLICATE(21002, "记录重复"),
    DB_REL_RECORD_NOT_FOUND(21003, "关联记录不存在"),
    AUTHENTICATION_ILLEGAL_REQUEST(40000, "非法请求"),
    AUTHENTICATION_FAIL(40100, "认证失败"),
    AUTHENTICATION_NOT_FOUND(40101, "账户不存在"),
    AUTHENTICATION_ERROR_PASS(40102, "密码错误"),
    AUTHENTICATION_DISABLE(40103, "账户被锁定"),
    AUTHENTICATION_ERROR_LIMIT(40104, "错误次数过多"),
    AUTHENTICATION_FORBIDDEN(40300, "禁止访问"),
    AUTHENTICATION_FORBIDDEN_DATA(40301, "禁止访问数据"),
    INVALID_PARAMETER(41200, "无效参数"),
    INVALID_PARAMETER_TYPE(41201, "参数类型或格式错误"),
    INVALID_PARAMETER_FIELD(41202, "属性校验失败"),

    COMPANY_NOT_EXIST(90000, "公司未指定或不存在"),
    EXIST_SUB_ORG(90005, "存在下级机构");


    public final int value;
    public final Series series;
    public final String reason;

    ErrorStatus(int value, String reason) {
        this.value = value;
        this.series = Series.valueOf(value);
        this.reason = reason;
    }

    public HttpStatus getHttpStatus() {
        if (series == Series.CLIENT) {
            try {
                return HttpStatus.resolve(value / 100);
            } catch (Exception e) {
                return HttpStatus.BAD_REQUEST;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


    public enum Series {
        CODE(0, "代码异常"),
        DB(2, "数据库异常"),
        CLIENT(4, "客户端异常"),
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
