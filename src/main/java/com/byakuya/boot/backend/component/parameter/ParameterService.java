package com.byakuya.boot.backend.component.parameter;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ganzl at 2022/4/28 17:27
 */
@Service
public class ParameterService {

    private static final String CAPI_GROUP_KEY = "capi";

    private static final String COS_GROUP_KEY = "cos";

    private static final String SMS_GROUP_KEY = "sms";
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
