package com.byakuya.boot.backend.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.io.IOException;
import java.lang.reflect.Type;

/** Created by ganzl on 2020/7/21. 动态过滤Json字段 */
public class DynamicJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
  DynamicJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper);
  }

  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    if (object instanceof MappingJacksonValue) {
      PropertyFilter filter =
          ((MappingJacksonValue) object)
              .getFilters()
              .findPropertyFilter(DynamicBeanPropertyFilter.DYNAMIC_FILTER_NAME, null);
      if (filter instanceof DynamicBeanPropertyFilter) {
        DynamicBeanPropertyFilter dynamicBeanPropertyFilter = (DynamicBeanPropertyFilter) filter;
        ObjectMapper copObjectMapper = defaultObjectMapper.copy();
        dynamicBeanPropertyFilter
            .getTargetClassSet()
            .forEach(target -> copObjectMapper.addMixIn(target, filter.getClass()));
        new MappingJackson2HttpMessageConverter(copObjectMapper) {
          void writeInternalExpose(Object object, Type type, HttpOutputMessage outputMessage)
              throws IOException, HttpMessageNotWritableException {
            writeInternal(object, type, outputMessage);
          }
        }.writeInternalExpose(object, type, outputMessage);
        return;
      }
    }
    super.writeInternal(object, type, outputMessage);
  }
}
