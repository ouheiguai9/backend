package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.SystemVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by 田伯光 at 2022/10/12 17:01
 */
@Data
@Accessors(chain = true)
public class ApiResourceVO implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;
    private String moduleCode;
    private String moduleName;
    private String methodCode;
    private String methodName;

    @JsonProperty
    public String getAuthKey() {
        return AuthorizationService.createAuthKey(moduleCode, methodCode);
    }

    @Override
    public int hashCode() {
        int result = moduleCode.hashCode();
        result = 31 * result + methodCode.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiResourceVO that = (ApiResourceVO) o;

        if (!moduleCode.equals(that.moduleCode)) return false;
        return methodCode.equals(that.methodCode);
    }
}
