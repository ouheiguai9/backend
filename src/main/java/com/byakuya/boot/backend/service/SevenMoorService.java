package com.byakuya.boot.backend.service;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class SevenMoorService {
    private static final ConcurrentMap<Long, Auth> AUTH_MAP = new ConcurrentHashMap<>();
    private final ParameterService parameterService;
    private final ObjectMapper objectMapper;

    private Function<? super Long, ? extends Auth> func;

    public SevenMoorService(ParameterService parameterService, ObjectMapper objectMapper) {
        this.parameterService = parameterService;
        this.objectMapper = objectMapper;
        func = aLong -> {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(parameterService.getParameters(aLong, "SevenMoor")), Auth.class);
            } catch (JsonProcessingException e) {
                log.error("Load 7moor info", e);
                return new Auth();
            }
        };
    }

    public void webCall(Long tenantId, String from, String to) {
        Auth auth = AUTH_MAP.computeIfAbsent(tenantId, func);
    }

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    public void refreshAuth() {
        AUTH_MAP.keySet().forEach(tenantId -> AUTH_MAP.put(tenantId, func.apply(tenantId)));
        log.info("refreshAuth");
    }

    @Data
    static class Auth {
        private String account = "N000*****91";//替换为您的账户
        private String secret = "5fdb6********bb88e4b";//替换为您的api密码
        private String host = "http://apis.7moor.com";

    }
}
