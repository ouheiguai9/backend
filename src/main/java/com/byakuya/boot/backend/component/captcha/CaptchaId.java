package com.byakuya.boot.backend.component.captcha;

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
public class CaptchaId implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private Tenant tenant;
    private String target;
    private Type captchaType;

    public CaptchaId setTenantId(Long id) {
        if (id != null) {
            Tenant tenant = new Tenant();
            tenant.setId(id);
            this.tenant = tenant;
        }
        return this;
    }
}
