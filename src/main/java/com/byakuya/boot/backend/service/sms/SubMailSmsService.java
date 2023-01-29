package com.byakuya.boot.backend.service.sms;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 田伯光 at 2022/11/13 0:55
 */
@Service
class SubMailSmsService implements ISmsService {

    private final ParameterService parameterService;
    private final RestTemplate restTemplate;

    public SubMailSmsService(ParameterService parameterService, RestTemplate restTemplate) {
        this.parameterService = parameterService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendLoginCaptcha(Long tenantId, String phone, String template, String captcha) throws BackendException {
        Map<String, Object> params = authParams(tenantId);
        params.put("to", phone);
        params.put("project", template);
        params.put("vars", "{\"code\":\"" + captcha + "\"}");
        sendTemplate(params);
    }

    private Map<String, Object> authParams(Long tenantId) {
//        params.put("appid", "88123");
//        params.put("signature", "0c9cd9726a38f26071c4c39a9616d490");
        return new HashMap<>(parameterService.getParameterMap(tenantId, SmsSender.SubMail.toString()));
    }

    private void sendTemplate(Map<String, Object> params) {
        try {
            MsgResponse msgResponse = restTemplate.postForEntity("https://api-v4.mysubmail.com/sms/xsend", params, MsgResponse.class).getBody();
            if (msgResponse == null || StringUtils.hasText(msgResponse.code)) {
                throw new BackendException(ErrorStatus.CODE_SMS);
            }
        } catch (RestClientException e) {
            throw new BackendException(ErrorStatus.CODE_SMS, e);
        }
    }

    @Data
    private static class MsgResponse {
        private String status;
        private String send_id;
        private String code;
        private String msg;
        private int fee;
    }
}
