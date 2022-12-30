package com.byakuya.boot.backend.service.sms;

import com.byakuya.boot.backend.exception.BackendException;

public interface ISmsService {
    void sendLoginCaptcha(Long tenantId, String phone, String template, String captcha) throws BackendException;
}
