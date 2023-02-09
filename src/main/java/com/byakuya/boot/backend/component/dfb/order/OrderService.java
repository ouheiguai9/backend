package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.customer.CustomerService;
import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.component.dfb.lawyer.LawyerService;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.utils.SnowFlakeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2023/2/9 15:29
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final LawyerService lawyerService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, LawyerService lawyerService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.lawyerService = lawyerService;
    }

    @Transactional
    public Order create(Long customerId, Long lawyerId) {
        Customer customer = customerService.query(customerId, false).orElseThrow(RecordNotFoundException::new);
        Lawyer lawyer = lawyerService.query(lawyerId, false).orElseThrow(RecordNotFoundException::new);
        lawyerService.beginWorking(lawyer);
        Order order = new Order();
        order.setCustomer(customer);
        order.setLawyer(lawyer);
        order.setCreateTime(LocalDateTime.now());
        order.setSerial(SnowFlakeUtils.newId());
        order.setState(OrderState.CREATED);
        return orderRepository.save(order);
    }
}
