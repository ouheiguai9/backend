package com.byakuya.boot.backend.component.dfb.customer;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/9/4 16:31
 */
interface CustomerRepository extends JpaRepository<Customer, Long> {
    @EntityGraph("Customer.User")
    Page<Customer> findAllByPhoneContains(Pageable pageable, String phone);

    @NotNull
    @EntityGraph("Customer.User")
    Page<Customer> findAll(@NotNull Pageable pageable);
}
