package com.byakuya.boot.backend.event;

import com.byakuya.boot.backend.SystemVersion;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.util.Collections;
import java.util.Map;

/**
 * Created by 相亲于盛夏 at 2023/2/5 20:56
 */
public class ParameterRefreshEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
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
