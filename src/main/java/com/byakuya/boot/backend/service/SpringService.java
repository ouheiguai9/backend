package com.byakuya.boot.backend.service;

import com.byakuya.boot.backend.service.sms.ISmsService;
import com.byakuya.boot.backend.service.sms.SmsSender;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class SpringService implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ISmsService getSmsService(SmsSender sender) {
        return applicationContext.getBean(sender.cls);
    }
}
