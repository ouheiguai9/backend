package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.config.ApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author ganzl
 */
@ApiModule(path = "parameters", name = "parameter", desc = "系统参数管理")
@Validated
class ParameterController {
    private final ParameterRepository parameterRepository;

    public ParameterController(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @ApiMethod(value = "add", desc = "增加", method = RequestMethod.POST, onlyAdmin = true)
    public ResponseEntity<Parameter> create(@Valid @RequestBody Parameter parameter) {
        return ResponseEntity.ok(parameterRepository.save(parameter));
    }

    @ApiMethod(value = "status", desc = "禁用/启用", path = "/{id}/{status}", method = RequestMethod.PATCH, onlyAdmin = true)
    public ResponseEntity<Parameter> lock(@PathVariable Long id, @PathVariable Boolean status) {
        Parameter old = get(id);
        old.setLocked(status);
        return ResponseEntity.ok(parameterRepository.save(old));
    }

    private Parameter get(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new BackendException(ErrorStatus.DB_RECORD_NOT_FOUND));
    }

    @ApiMethod(value = "read", desc = "查询", method = RequestMethod.GET, onlyAdmin = true)
    public ResponseEntity<Iterable<Parameter>> read() {
        return ResponseEntity.ok(parameterRepository.findAll());
    }

    @ApiMethod(value = "update", desc = "修改", method = RequestMethod.PUT, onlyAdmin = true)
    public ResponseEntity<Parameter> update(@Valid @RequestBody Parameter parameter) {
        Parameter old = get(parameter.getId());
        old.setGroupKey(parameter.getGroupKey());
        old.setItemKey(parameter.getItemKey());
        old.setItemValue(parameter.getItemValue());
        old.setOrdering(parameter.getOrdering());
        old.setDescription(parameter.getDescription());
        return ResponseEntity.ok(parameterRepository.save(old));
    }
}
