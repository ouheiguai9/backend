package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.validation.annotation.Validated;

@ApiModule(path = "callback", secure = false)
@Validated
public class Callback {
}
