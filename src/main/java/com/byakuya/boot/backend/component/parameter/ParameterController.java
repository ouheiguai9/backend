package com.byakuya.boot.backend.component.parameter;

import com.byakuya.boot.backend.config.ResAPI;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author ganzl
 */
@RestController("parameters")
@Validated
public class ParameterController {
    private final ParameterRepository parameterRepository;

    public ParameterController(ParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @PostMapping
    @ResAPI(module = "system", code = "create-parameter", desc = "创建系统参数", onlyAdmin = true)
    public ResponseEntity<Parameter> create(@Valid @RequestBody Parameter parameter) {
        return ResponseEntity.ok(parameterRepository.save(parameter));
    }

    @PostMapping(value = "/locked")
    @ResAPI(module = "system", code = "lock-parameter", desc = "启/禁用系统参数", onlyAdmin = true)
    public ResponseEntity<Parameter> lock(@NotBlank Long id, boolean locked) {
        Parameter old = get(id);
        old.setLocked(locked);
        return ResponseEntity.ok(parameterRepository.save(old));
    }

    private Parameter get(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new BackendException(ErrorStatus.DB_RECORD_NOT_FOUND));
    }

    @GetMapping
    @ResAPI(module = "system", code = "read-parameter", desc = "查看全部系统参数", onlyAdmin = true)
    public ResponseEntity<Iterable<Parameter>> read() {
        return ResponseEntity.ok(parameterRepository.findAll());
    }

    @PutMapping
    @ResAPI(module = "system", code = "update-parameter", desc = "修改系统参数", onlyAdmin = true)
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
