package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
@Service
public class ParameterService {
    private static final String ADMIN_RANDOM_KEY = "admin-random-key";
    private final ParameterRepository parameterRepository;
    private final Environment environment;
    private final ObjectMapper objectMapper;

    public ParameterService(ParameterRepository parameterRepository, Environment environment, ObjectMapper objectMapper) {
        this.parameterRepository = parameterRepository;
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    public String getAdminRandomKey() {
        Parameter parameter = parameterRepository.findByTenant_idAndGroupKeyAndItemKey(null, ADMIN_RANDOM_KEY, ADMIN_RANDOM_KEY).orElseGet(() -> {
            Parameter tmp = new Parameter();
            tmp.setGroupKey(ADMIN_RANDOM_KEY);
            tmp.setItemKey(ADMIN_RANDOM_KEY);
            tmp.setItemValue(UUID.randomUUID().toString());
            return parameterRepository.save(tmp);
        });
        if (Arrays.asList(environment.getActiveProfiles()).contains(ConstantUtils.ACTIVE_PRO_KEY)) {
            LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1);
            if (oneDayBefore.isBefore(parameter.getLastModifiedDate().orElse(oneDayBefore))) {
                return parameter.getItemValue();
            } else {
                parameter.setItemValue(UUID.randomUUID().toString());
                return parameterRepository.save(parameter).getItemValue();
            }
        }
        return parameter.getItemValue();
    }


    public Map<String, String> getParameterMap(Long tenantId, String group) {
        HashMap<String, String> rtnVal = new HashMap<>();
        getParameters(tenantId, group).forEach(param -> rtnVal.put(param.getItemKey(), param.getItemValue()));
        return rtnVal;
    }

    public List<Parameter> getParameters(Long tenantId, String group) {
        return parameterRepository.findByTenant_idAndGroupKeyOrderByOrderingAsc(tenantId, group);
    }

    public <T> T getForConfig(Long tenantId, String group, Class<T> configType) {
        return object2Config(getParameters(tenantId, group), configType);
    }

    public <T> T object2Config(Object o, Class<T> configType) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(o), configType);
        } catch (Exception e) {
            return null;
        }
    }

    public Parameter add(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Transactional
    public Parameter modifyStatus(Long id, Boolean status) {
        Parameter old = get(id);
        old.setLocked(status);
        return parameterRepository.save(old);
    }

    private Parameter get(Long id) {
        return parameterRepository.findById(id).orElseThrow(RecordNotFoundException::new);
    }

    @Transactional
    public Parameter modify(Parameter parameter) {
        Parameter old = get(parameter.getId());
        old.setGroupKey(parameter.getGroupKey());
        old.setItemKey(parameter.getItemKey());
        old.setItemValue(parameter.getItemValue());
        old.setOrdering(parameter.getOrdering());
        old.setDescription(parameter.getDescription());
        return parameterRepository.save(old);
    }
}
