package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 田伯光 at 2022/10/12 2:47
 */
@ApiModule(path = "authorizations")
class AuthorizationController {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    AuthorizationController(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @GetMapping("acl")
    public List<ApiResourceVO> acl() {
        return requestMappingHandlerMapping.getHandlerMethods().values().stream().filter(item -> {
            AclApiMethod method = item.getMethodAnnotation(AclApiMethod.class);
            return method != null && !method.onlyAdmin() && item.getBeanType().isAnnotationPresent(AclApiModule.class);
        }).map(item -> {
            AclApiModule module = item.getBeanType().getAnnotation(AclApiModule.class);
            AclApiMethod method = item.getMethodAnnotation(AclApiMethod.class);
            assert method != null;
            return new ApiResourceVO().setModuleCode(module.value()).setModuleName(module.desc()).setMethodCode(method.value()).setMethodName(method.desc());
        }).distinct().sorted(Comparator.comparing(ApiResourceVO::getAuthKey)).collect(Collectors.toList());
    }
}
