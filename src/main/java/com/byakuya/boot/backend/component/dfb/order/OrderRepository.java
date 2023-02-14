package com.byakuya.boot.backend.component.dfb.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/9 21:02
 */
interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByCustomer_idOrderByCreateTimeDesc(Long customerId);
}
