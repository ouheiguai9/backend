package com.byakuya.boot.backend.component.captcha;

import com.byakuya.boot.backend.SystemVersion;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/17 16:12
 */
@Data
@Accessors(chain = true)
@Embeddable
public class CaptchaId implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private Long tenantId;
    private String target;
    private Type captchaType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaptchaId captchaId = (CaptchaId) o;

        if (!tenantId.equals(captchaId.tenantId)) return false;
        if (!target.equals(captchaId.target)) return false;
        return captchaType == captchaId.captchaType;
    }

    @Override
    public int hashCode() {
        int result = tenantId.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + captchaType.hashCode();
        return result;
    }
}
