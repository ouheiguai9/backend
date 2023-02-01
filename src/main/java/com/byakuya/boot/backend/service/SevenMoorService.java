package com.byakuya.boot.backend.service;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Service
public class SevenMoorService {
    private static final ConcurrentHashMap<Long, SevenMoor> AUTH_MAP = new ConcurrentHashMap<>();
    private final ParameterService parameterService;
    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    private Function<? super Long, ? extends SevenMoor> func;

    public SevenMoorService(ParameterService parameterService, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.parameterService = parameterService;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        func = aLong -> {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(parameterService.getParameters(aLong, "SevenMoor")), SevenMoor.class).computeAuth();
            } catch (Exception e) {
                log.error("Load 7moor info", e);
                return null;
            }
        };
    }

    public boolean webCall(Long tenantId, String from, String to, String actionId) {
        SevenMoor auth = AUTH_MAP.computeIfAbsent(tenantId, func);
        if (auth == null) return false;
        try {
            restTemplate.postForEntity(auth.createUrl("/v20160818/webCall/webCall/"), null, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES)
    public void refreshAuth() {
        AUTH_MAP.keySet().forEach(tenantId -> AUTH_MAP.put(tenantId, func.apply(tenantId)));
        log.info("refreshAuth");
    }

    @Data
    static class SevenMoor {
        private String account;
        private String secret;
        private String host;
        private String sig;
        private String auth;
        private String timestamp;

        public SevenMoor computeAuth() {
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            sig = DigestUtils.md5DigestAsHex((account + secret + timestamp).getBytes(StandardCharsets.UTF_8)).toUpperCase();
            auth = Base64Utils.encodeToString((account + ":" + timestamp).getBytes(StandardCharsets.UTF_8));
            return this;
        }

        public String createUrl(String interfacePath) {
            return host + interfacePath + account + "?sig=" + sig;
        }
    }
}
