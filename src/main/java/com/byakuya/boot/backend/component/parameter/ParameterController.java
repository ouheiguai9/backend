package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.event.ParameterRefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author ganzl
 */
@AclApiModule(path = "parameters", value = "parameter", desc = "系统参数管理")
@Validated
class ParameterController {
    private final ParameterService parameterService;
    private final ApplicationContext applicationContext;

    public ParameterController(ParameterService parameterService, ApplicationContext applicationContext) {
        this.parameterService = parameterService;
        this.applicationContext = applicationContext;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST, onlyAdmin = true)
    public Parameter create(@Valid @RequestBody Parameter parameter) {
        return parameterService.add(parameter);
    }

    @AclApiMethod(value = "refresh", desc = "刷新", path = "/refresh", method = RequestMethod.POST, onlyAdmin = true)
    public void refresh(@RequestParam(required = false) Long tenantId, @NotBlank String groupKey) {
        Map<String, String> valueMap = parameterService.getParameterMap(tenantId, groupKey);
        if (valueMap.isEmpty()) return;
        applicationContext.publishEvent(new ParameterRefreshEvent(tenantId, groupKey, valueMap));
    }

    @AclApiMethod(value = "status", desc = "禁用/启用", path = "/{id}/{status}", method = RequestMethod.PATCH, onlyAdmin = true)
    public Parameter lock(@PathVariable Long id, @PathVariable Boolean status) {
        return parameterService.modifyStatus(id, status);
    }

    @AclApiMethod(value = "update", desc = "修改", method = RequestMethod.PUT, onlyAdmin = true)
    public Parameter update(@Valid @RequestBody Parameter parameter) {
        return parameterService.modify(parameter);
    }
}
