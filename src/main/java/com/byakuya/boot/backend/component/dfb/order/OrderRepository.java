package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/9 21:02
 */
interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByCustomer_idOrderByCreateTimeDesc(Long customerId);

    @Query("select o from Order o where o.id in (select max(o.id) from Order o where o.customer.id in ?1 group by o.customer)")
    List<Order> queryCustomerLastOrder(Iterable<Long> customerIds);

    @Query("select o.lawyer, count(o.id) as orderCount from Order o where o.lawyer.id in ?1 group by o.lawyer")
    List<LawyerOrderStat> queryLawyerOrderStat(Iterable<Long> lawyerIds);

    interface LawyerOrderStat {
        Lawyer getLawyer();

        Long getOrderCount();
    }
}
