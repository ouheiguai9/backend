package com.byakuya.boot.backend.component.dfb.customer;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 田伯光 at 2022/9/4 16:31
 */
interface CustomerRepository extends JpaRepository<Customer, Long> {
}
