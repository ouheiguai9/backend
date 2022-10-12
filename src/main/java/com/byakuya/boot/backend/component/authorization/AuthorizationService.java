package com.byakuya.boot.backend.component.authorization;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by 田伯光 at 2022/10/12 21:22
 */
@Service
public class AuthorizationService {
    private final AuthorizationRepository authorizationRepository;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public AuthorizationService(AuthorizationRepository authorizationRepository, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.authorizationRepository = authorizationRepository;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public static String createAuthKey(String module, String method) {
        return module + ":" + method;
    }

    public List<ApiResourceVO> aclAll() {
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

    @Transactional(readOnly = true)
    public Set<String> queryApiAuth(Iterable<Long> subjects) {
        return authorizationRepository.findAllBySubjectIdInAndAuthType(subjects, Authorization.AuthType.API).map(Authorization::getContent).collect(Collectors.toSet());
    }
}
