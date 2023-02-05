package com.byakuya.boot.backend.event;

import org.springframework.context.ApplicationEvent;

import java.util.Collections;
import java.util.Map;

/**
 * Created by 相亲于盛夏 at 2023/2/5 20:56
 */
public class ParameterRefreshEvent extends ApplicationEvent {
    private static final long serialVersionUID = 5738752143275243L;
    public final Long tenantId;
    public final String groupKey;

    private final Map<String, String> valueMap;

    public ParameterRefreshEvent(Long tenantId, String groupKey, Map<String, String> valueMap) {
        super(valueMap);
        this.tenantId = tenantId;
        this.groupKey = groupKey;
        this.valueMap = valueMap;
    }

    @Override
    public Object getSource() {
        return getValueMap();
    }

    public Map<String, String> getValueMap() {
        return Collections.unmodifiableMap(valueMap);
    }
}
