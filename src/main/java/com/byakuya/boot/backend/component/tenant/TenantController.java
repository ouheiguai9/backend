package com.byakuya.boot.backend.component.tenant;

import com.byakuya.boot.backend.config.ApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by 田伯光 at 2022/10/9 21:38
 */
@ApiModule(path = "tenants", name = "tenants", desc = "租户管理")
@Validated
class TenantController {
    private final TenantRepository tenantRepository;

    TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @ApiMethod(value = "add", desc = "增加", method = RequestMethod.POST, onlyAdmin = true)
    public ResponseEntity<Tenant> create(@Valid @RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantRepository.save(tenant));
    }
}
