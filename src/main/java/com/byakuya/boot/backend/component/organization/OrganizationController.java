package com.byakuya.boot.backend.component.organization;

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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/9/12 21:19
 */
@AclApiModule(path = "organizations", value = "organization", desc = "组织机构管理")
@Validated
class OrganizationController {
    private final OrganizationRepository organizationRepository;

    OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public ResponseEntity<Organization> create(@Valid @RequestBody Organization organization, AccountAuthentication authentication) {
        if (Objects.nonNull(organization.getParentId())) {
            Organization parent = get(organization.getParentId());
            organization.setParent(parent);
            organization.setLevel(parent.getLevel() + 1);
            Set<Organization> ancestors = new HashSet<>();
            boolean canAdd = AccountAuthentication.isAdmin(authentication);
            do {
                canAdd = canAdd || (Long.valueOf(authentication.getTenantId()).equals(parent.getId()));
                ancestors.add(parent);
                parent = parent.getParent();
            } while (parent != null);
            if (!canAdd) {
                //非超级用户只能增加自己所属机构的下级机构
                throw new BackendException(ErrorStatus.AUTHENTICATION_FORBIDDEN_DATA);
            }
            organization.setAncestors(ancestors);
        } else {
            if (!AccountAuthentication.isAdmin(authentication)) {
                //非超级用户不能创建顶层组织机构
                throw new BackendException(ErrorStatus.AUTHENTICATION_FORBIDDEN_DATA);
            }
            organization.setLevel(1);
        }
        organization.setId(null);
        return ResponseEntity.ok(organizationRepository.save(organization));
    }

    @AclApiMethod(value = "status", desc = "禁用/启用", path = "/{id}/{status}", method = RequestMethod.PATCH, onlyAdmin = true)
    public ResponseEntity<Organization> lock(@PathVariable Long id, @PathVariable Boolean status) {
        Organization old = get(id);
        if (!status && old.getDescendants().stream().anyMatch(x -> !x.isLocked())) {
            throw new BackendException(ErrorStatus.EXIST_SUB_ORG);
        }
        old.setLocked(status);
        return ResponseEntity.ok(organizationRepository.save(old));
    }

    @AclApiMethod(value = "read", desc = "查询", path = {"/parent/{id}", "/parent"}, method = RequestMethod.GET, onlyAdmin = true)
    public ResponseEntity<Iterable<Organization>> readByParent(@PathVariable(required = false) Long id) {
        return ResponseEntity.ok(organizationRepository.findByParent_id(id));
    }

    @AclApiMethod(value = "read", desc = "查询", path = "/{id}", method = RequestMethod.GET, onlyAdmin = true)
    public ResponseEntity<Organization> read(@PathVariable Long id) {
        return ResponseEntity.ok(get(id));
    }

    private Organization get(Long id) {
        return organizationRepository.findById(id).orElseThrow(() -> new BackendException(ErrorStatus.DB_RECORD_NOT_FOUND));
    }
}
