package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.component.organization.OrganizationRepository;
import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by 田伯光 at 2022/10/6 22:16
 */
@AclApiModule(path = "roles", value = "role", desc = "角色管理")
@Validated
class RoleController {
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;

    RoleController(RoleRepository roleRepository, OrganizationRepository organizationRepository) {
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public ResponseEntity<Role> create(@Valid @RequestBody Role role, AccountAuthentication authentication) {
        return ResponseEntity.ok(roleRepository.save(role));
    }

    @AclApiMethod(value = "read", desc = "查询", path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Role> read(@PathVariable Long id) {
        return ResponseEntity.ok(get(id));
    }

    private Role get(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new BackendException(ErrorStatus.DB_RECORD_NOT_FOUND));
    }
}
