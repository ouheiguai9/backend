package com.byakuya.boot.backend.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.byakuya.boot.backend.component.tenant.TenantCache;
import com.byakuya.boot.backend.event.ParameterRefreshEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * Created by 相亲于盛夏 at 2023/2/5 18:04
 */
@Service
public class AlipayService implements ApplicationListener<ParameterRefreshEvent> {
    public static final String GROUP_KEY = "alipay";
    private final TenantCache<AlipayClient> tenantCache;
    private final ParameterService parameterService;

    public AlipayService(ParameterService parameterService) {
        this.parameterService = parameterService;
        this.tenantCache = new TenantCache<>(tenantId -> {
            AlipayConfig config = parameterService.getForConfig(tenantId, GROUP_KEY, AlipayConfig.class);
            try {
                return new DefaultAlipayClient(config);
            } catch (AlipayApiException e) {
                return null;
            }
        });
    }

    @Override
    public void onApplicationEvent(ParameterRefreshEvent event) {
        if (event.tenantId == null || !GROUP_KEY.equals(event.groupKey)) return;
        AlipayConfig config = parameterService.object2Config(event.getValueMap(), AlipayConfig.class);
        try {
            tenantCache.reset(event.tenantId, new DefaultAlipayClient(config));
        } catch (AlipayApiException e) {
            tenantCache.remove(event.tenantId);
        }
    }
}
