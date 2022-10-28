package com.byakuya.boot.backend.component.unique;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.tenant.Tenant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/17 16:12
 */
@Data
@Accessors(chain = true)
public class UniqueId implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private Tenant tenant;
    private Type uniqueType;
    private String uniqueValue;
}
