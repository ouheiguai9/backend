package com.byakuya.boot.backend.component.organization;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/9/4 16:31
 */
interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Iterable<Organization> findByParent_id(Long parent_id);
}
