package com.byakuya.boot.backend.service.sms;

public enum SmsSender {
    SubMail(SubMailSmsService.class);

    public final Class<? extends ISmsService> cls;

    SmsSender(Class<? extends ISmsService> cls) {
        this.cls = cls;
    }
}
