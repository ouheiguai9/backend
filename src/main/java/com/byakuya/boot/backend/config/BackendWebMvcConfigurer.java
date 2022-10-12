package com.byakuya.boot.backend.config;

import com.byakuya.boot.backend.utils.ConstantUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * Created by 田伯光 at 2022/8/23 20:50
 */
@Configuration
public class BackendWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ConstantUtils.OPEN_API_PREFIX, cls -> Optional.of(cls).filter(x -> x.isAnnotationPresent(ApiModule.class)).map(x -> x.getAnnotation(ApiModule.class)).map(api -> !api.secure()).orElse(false));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
