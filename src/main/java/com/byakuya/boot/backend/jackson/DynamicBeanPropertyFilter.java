package com.byakuya.boot.backend.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.*;

/**
 * Created by ganzl on 2020/7/17.
 */
@JsonFilter(DynamicBeanPropertyFilter.DYNAMIC_FILTER_NAME)
public class DynamicBeanPropertyFilter extends SimpleBeanPropertyFilter {
    static final String DYNAMIC_FILTER_NAME = "JsonDynamicViewFilter";

    void addAnnotation(DynamicJsonView annotation) {
        if (annotation.include().length > 0) {
            includeMap.put(annotation.type(), new HashSet<>(Arrays.asList(annotation.include())));
        } else if (annotation.exclude().length > 0) {
            excludeMap.put(annotation.type(), new HashSet<>(Arrays.asList(annotation.exclude())));
        }
    }

    Set<Class<?>> getTargetClassSet() {
        if (isEmpty()) return Collections.emptySet();
        HashSet<Class<?>> rtnVal = new HashSet<>(excludeMap.keySet());
        rtnVal.addAll(includeMap.keySet());
        return rtnVal;
    }

    public boolean isEmpty() {
        return includeMap.isEmpty() && excludeMap.isEmpty();
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (includeFieldAtBean(writer.getName(), pojo)) {
            writer.serializeAsField(pojo, jgen, provider);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }

    }

    private boolean includeFieldAtBean(String fieldName, Object bean) {
        Set<String> fields = includeMap.get(bean.getClass());
        if (fields != null) {
            return fields.contains(fieldName);
        }
        fields = excludeMap.get(bean.getClass());
        if (fields != null) {
            return !fields.contains(fieldName);
        }
        return true;
    }

    private Map<Class<?>, Set<String>> excludeMap = new HashMap<>();
    private Map<Class<?>, Set<String>> includeMap = new HashMap<>();
}
