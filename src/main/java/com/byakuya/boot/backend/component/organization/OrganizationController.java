package com.byakuya.boot.backend.component.organization;

import com.byakuya.boot.backend.config.ApiMethod;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.exception.InvalidParameterException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by 田伯光 at 2022/9/12 21:19
 */
@ApiModule(path = "organizations", name = "organization", desc = "组织机构管理")
@Validated
class OrganizationController {
    private final OrganizationRepository organizationRepository;

    OrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @ApiMethod(value = "add", desc = "增加", method = RequestMethod.POST)
    public ResponseEntity<Organization> create(@Valid @RequestBody Organization organization, @AuthenticationPrincipal AccountAuthentication authentication) {
        if (Objects.nonNull(organization.getParentId())) {
            Organization parent = organizationRepository.findById(organization.getParentId()).orElseThrow(() -> InvalidParameterException.build("上级机构不存在"));
            organization.setParent(parent);
            organization.setLevel(parent.getLevel() + 1);
            Set<Organization> ancestors = new HashSet<>();
            boolean canAdd = AccountAuthentication.isAdmin(authentication);
            do {
                canAdd = canAdd || (Long.valueOf(authentication.getCompanyId()).equals(parent.getId()));
                ancestors.add(parent);
                parent = parent.getParent();
            } while (parent != null);
            if (!canAdd) {
                throw InvalidParameterException.build("只能新增下级机构");
            }
            organization.setAncestors(ancestors);
        } else {
            if (AccountAuthentication.isAdmin(authentication)) {
                throw InvalidParameterException.build("非超级用户不能创建顶层机构");
            }
            organization.setLevel(1);
        }
        organization.setId(null);
        return ResponseEntity.ok(organizationRepository.save(organization));
    }
}
