package com.byakuya.boot.backend.service;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.byakuya.boot.backend.component.tenant.TenantCache;
import com.byakuya.boot.backend.event.ParameterRefreshEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class SevenMoorService implements ApplicationListener<ParameterRefreshEvent> {
    public static final String GROUP_KEY = "SevenMoor";
    private final TenantCache<Config> tenantCache;

    private final ParameterService parameterService;

    private final RestTemplate restTemplate;

    public SevenMoorService(ParameterService parameterService, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.parameterService = parameterService;
        this.tenantCache = new TenantCache<>(tenantId -> parameterService.getForConfig(tenantId, GROUP_KEY, Config.class));
    }

    public boolean webCall(Long tenantId, String from, String to, String actionId) {
        Config config = tenantCache.get(tenantId);
        if (config == null) return false;
        Auth auth = config.getAuth();
        try {
            restTemplate.postForEntity(auth.createUrl("/v20160818/webCall/webCall/"), null, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onApplicationEvent(ParameterRefreshEvent event) {
        if (event.tenantId == null || !GROUP_KEY.equals(event.groupKey)) return;
        tenantCache.reset(event.tenantId, parameterService.object2Config(event.getValueMap(), Config.class));
    }

    @Data
    static class Config {
        private String account;
        private String secret;
        private String host;
        @JsonIgnore
        private SoftReference<Auth> refAuth;

        public Auth getAuth() {
            Auth auth = refAuth.get();
            if (auth != null && auth.isValid()) return auth;
            auth = new Auth(this);
            refAuth = new SoftReference<>(auth);
            return auth;
        }
    }

    static class Auth {
        public final Config config;
        public final String sig;
        public final String auth;
        public final String timestamp;
        public final LocalDateTime end;


        Auth(Config config) {
            LocalDateTime now = LocalDateTime.now();
            this.config = config;
            this.timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            this.end = now.plusMinutes(3);
            this.sig = DigestUtils.md5DigestAsHex((config.account + config.secret + timestamp).getBytes(StandardCharsets.UTF_8)).toUpperCase();
            this.auth = Base64Utils.encodeToString((config.account + ":" + timestamp).getBytes(StandardCharsets.UTF_8));
        }

        boolean isValid() {
            return end.isAfter(LocalDateTime.now());
        }

        public String createUrl(String interfacePath) {
            return config.host + interfacePath + config.account + "?sig=" + sig;
        }
    }
}
