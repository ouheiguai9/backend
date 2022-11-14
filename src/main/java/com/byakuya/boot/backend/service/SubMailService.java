package com.byakuya.boot.backend.service;

import com.byakuya.boot.backend.component.parameter.ParameterService;
import org.springframework.stereotype.Service;

/**
 * Created by 田伯光 at 2022/11/13 0:55
 */
@Service
public class SubMailService {
    private final ParameterService parameterService;

    public SubMailService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
