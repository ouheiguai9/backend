package com.byakuya.boot.backend.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import java.util.Arrays;

/**
 * Created by ganzl on 2020/7/17.
 */
@RestControllerAdvice
public class DynamicJacksonResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return super.supports(returnType, converterType)
                && (returnType.hasMethodAnnotation(DynamicJsonView.class)
                || returnType.hasMethodAnnotation(DynamicJsonViews.class));
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        final DynamicBeanPropertyFilter filter = new DynamicBeanPropertyFilter();
        Arrays.asList(returnType.getMethodAnnotations()).forEach(annotation -> {
            if (annotation instanceof DynamicJsonView) {
                filter.addAnnotation((DynamicJsonView) annotation);
            } else if (annotation instanceof DynamicJsonViews) {
                Arrays.asList(((DynamicJsonViews) annotation).value()).forEach(filter::addAnnotation);
            }
        });
        if (!filter.isEmpty()) {
            SimpleFilterProvider provider = new SimpleFilterProvider();
            provider.addFilter(DynamicBeanPropertyFilter.DYNAMIC_FILTER_NAME, filter);
            bodyContainer.setFilters(provider);
        }
    }

    @Bean
    public static MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new DynamicJackson2HttpMessageConverter(objectMapper);
    }

}
