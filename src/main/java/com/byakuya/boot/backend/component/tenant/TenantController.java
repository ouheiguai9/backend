package com.byakuya.boot.backend.component.tenant;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.security.TenantPrefixMatcher;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by 田伯光 at 2022/10/9 21:38
 */
@AclApiModule(path = "tenants", value = "tenants", desc = "租户管理")
@Validated
class TenantController {
    private static final Trie TRIE = new Trie();
    private final TenantRepository tenantRepository;

    TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @AclApiMethod(value = "add", desc = "增加", method = RequestMethod.POST, onlyAdmin = true)
    public ResponseEntity<Tenant> create(@Valid @RequestBody Tenant tenant) {
        tenant.setNew(true);
        return ResponseEntity.ok(tenantRepository.save(tenant));
    }

    @Bean
    TenantPrefixMatcher tenantPrefixMatcher() {
        tenantRepository.findAllByPrefixIsNotNull().forEach(tenant -> TRIE.add(tenant.getPrefix(), tenant.getId()));
        return request -> {
            String servletPath = request.getServletPath();
            Trie node = TRIE;
            char ch;
            for (int i = 1, len = servletPath.length(), index; i < len; i++) {
                ch = servletPath.charAt(i);
                if (ch == '/') {
                    return node.tenantId;
                }
                index = ch - 'a';
                if (node.next == null || node.next[index] == null) {
                    return null;
                }
                node = node.next[index];
            }
            return node.tenantId;
        };
    }

    static class Trie {
        Trie[] next;
        Long tenantId;

        void add(String prefix, Long tenantId) {
            Trie node = this;
            for (int i = 0, len = prefix.length(), index; i < len; i++) {
                index = prefix.charAt(i) - 'a';
                if (node.next == null) {
                    node.next = new Trie[26];
                }
                if (node.next[index] == null) {
                    node.next[index] = new Trie();
                }
                node = node.next[index];
            }
            node.tenantId = tenantId;
        }
    }
}
