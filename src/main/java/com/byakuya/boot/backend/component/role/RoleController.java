package com.byakuya.boot.backend.component.role;

import com.byakuya.boot.backend.component.organization.OrganizationRepository;
import com.byakuya.boot.backend.config.ApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Created by 田伯光 at 2022/10/6 22:16
 */
@ApiModule(path = "roles", name = "roles", desc = "角色管理")
@Validated
class RoleController {
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;

    RoleController(RoleRepository roleRepository, OrganizationRepository organizationRepository) {
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
    }

    @ApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public ResponseEntity<Role> create(@Valid @RequestBody Role role, AccountAuthentication authentication) {
        if (AccountAuthentication.isAdmin(authentication)) {
            if (Objects.isNull(role.getCompanyId())) {
                throw new BackendException(ErrorStatus.COMPANY_NOT_EXIST);
            }
        } else {
            role.setCompanyId(authentication.getCompanyId());
        }
        return ResponseEntity.ok(roleRepository.save(role));
    }

    private Role get(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new BackendException(ErrorStatus.DB_RECORD_NOT_FOUND));
    }
}
