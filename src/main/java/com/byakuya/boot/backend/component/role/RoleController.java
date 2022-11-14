package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.exception.ValidationFailedException;
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

    RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) {
        checkRoleNameUnique(role);
        return ResponseEntity.ok(roleRepository.save(role));
    }

    private void checkRoleNameUnique(Role role) throws ValidationFailedException {
        if (roleRepository.existsByNameAndTenant_id(role.getName(), role.getTenantId())) {
            throw ValidationFailedException.buildWithCode("error.validation.role.name.exists");
        }
    }

    @AclApiMethod(value = "read", desc = "查询", path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Role> read(@PathVariable Long id) {
        return ResponseEntity.ok(get(id));
    }

    private Role get(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("error.db.record.not.found.role", id));
    }
}
