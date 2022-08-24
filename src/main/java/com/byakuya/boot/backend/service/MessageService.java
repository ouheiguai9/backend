package com.byakuya.boot.backend.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by ganzl at 2022/4/28 15:09
 */
@Service
public class MessageService {
    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String message(String key) {
        return message(key, null);
    }

    public String message(String key, Object[] args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}
