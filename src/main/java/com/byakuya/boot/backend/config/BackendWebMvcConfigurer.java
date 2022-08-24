package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by 田伯光 at 2022/8/23 20:50
 */
@Configuration
public class BackendWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ConstantUtils.REST_API_PREFIX, cls -> cls.isAnnotationPresent(RestAPIController.class));
    }
}
