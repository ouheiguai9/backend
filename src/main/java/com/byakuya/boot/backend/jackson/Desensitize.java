package com.byakuya.boot.backend.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})  // 针对成员属性进行脱敏
@JacksonAnnotationsInside  // 表示和其他Jackson注解联合使用，如果缺少则无法执行数据脱敏流程
@JsonSerialize(using = DesensitizeJsonSerializer.class)  // 表明使用的序列化的类，定义在后面
public @interface Desensitize {
    /**
     * 对数据的脱敏策略
     *
     * @return 脱敏策略
     */
    DesensitizeStrategy strategy();

    enum DesensitizeStrategy {
        /**
         * 对用户名进行脱敏，基于正则表达式实现
         */
        NAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
        /**
         * 对律师进行脱敏
         */
        LAWYER(s -> s.replaceAll("(\\S)\\S*", "$1律师")),

        /**
         * 对身份证进行脱敏
         */
        ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),

        /**
         * 对电话号码进行脱敏
         */
        PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

        /**
         * 对地址进行脱敏
         */
        ADDRESS(s -> s.replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****")),

        /**
         * 对密码进行脱敏，全部加密即可
         */
        PASSWORD(s -> "********");

        private final Function<String, String> desensitizeSerializer;

        DesensitizeStrategy(Function<String, String> desensitizeSerializer) {
            this.desensitizeSerializer = desensitizeSerializer;
        }

        // 用于后续获取脱敏的规则，实现脱敏
        public Function<String, String> desensitizeSerializer() {
            return desensitizeSerializer;
        }
    }
}

