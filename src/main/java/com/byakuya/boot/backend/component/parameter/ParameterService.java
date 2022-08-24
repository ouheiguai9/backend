package com.byakuya.boot.backend.component.parameter;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 田伯光 at 2022/4/28 17:27
 */
@Service
public class ParameterService {

    private static final String CAPI_GROUP_KEY = "capi";

    private static final String COS_GROUP_KEY = "cos";

    private static final String SMS_GROUP_KEY = "sms";
    private static final String ADMIN_RANDOM_KEY = "admin-random-key";
    private final ParameterRepository parameterRepository;

    public ParameterService(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    public String getValue(String group, String item) {
        return parameterRepository.findByGroupKeyAndItemKey(group, item).map(Parameter::getItemValue).orElse("");
    }

    public List<Parameter> getParameters(String group) {
        return parameterRepository.findByGroupKeyOrderByOrderingAsc(group);
    }

    public String getAdminRandomKey() {
        Parameter parameter = parameterRepository.findByGroupKeyAndItemKey(ADMIN_RANDOM_KEY, ADMIN_RANDOM_KEY).orElseGet(() -> {
            Parameter tmp = new Parameter();
            tmp.setGroupKey(ADMIN_RANDOM_KEY);
            tmp.setItemKey(ADMIN_RANDOM_KEY);
            tmp.setItemValue(UUID.randomUUID().toString());
            return parameterRepository.save(tmp);
        });
        LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1);
        if (oneDayBefore.isBefore(parameter.getLastModifiedDate().orElse(oneDayBefore))) {
            return parameter.getItemValue();
        } else {
            parameter.setItemValue(UUID.randomUUID().toString());
            return parameterRepository.save(parameter).getItemValue();
        }
    }

    public Map<String, String> getSMSMap() {
        return getParameterMap(SMS_GROUP_KEY);
    }

    public Map<String, String> getParameterMap(String group) {
        HashMap<String, String> rtnVal = new HashMap<>();
        getParameters(group).forEach(param -> rtnVal.put(param.getItemKey(), param.getItemValue()));
        return rtnVal;
    }

    public List<Parameter> getTencentCloudCAPI() {
        return getParameters(CAPI_GROUP_KEY);
    }

    public List<Parameter> getTencentCloudCOS() {
        return getParameters(COS_GROUP_KEY);
    }

    public String getTencentCloudSecretId() {
        return getValue(CAPI_GROUP_KEY, "SecretId");
    }

    public String getTencentCloudSecretKey() {
        return getValue(CAPI_GROUP_KEY, "SecretKey");
    }
}
