package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.customer.CustomerService;
import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.component.dfb.lawyer.LawyerService;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.utils.SnowFlakeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by 田伯光 at 2023/2/9 15:29
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final LawyerService lawyerService;
    private final EvaluationRepository evaluationRepository;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, LawyerService lawyerService, EvaluationRepository evaluationRepository) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.lawyerService = lawyerService;
        this.evaluationRepository = evaluationRepository;
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

    @Transactional
    public Evaluation addEvaluation(Evaluation evaluation, Long customerId) {
        if (evaluation.getOrderId() != null) {
            Order order = orderRepository.findById(evaluation.getOrderId()).orElseThrow(RecordNotFoundException::new);
            if (!Objects.equals(order.getCustomer().getId(), customerId)) {
                throw AuthException.forbidden(null);
            }
            evaluation.setOrder(order);
            evaluation.setCustomer(order.getCustomer().getPhone());
            evaluation.setLawyer(order.getLawyer().getName());
        } else {
            evaluation.setOrder(null);
        }
        if (!StringUtils.hasText(evaluation.getCustomer()) || !StringUtils.hasText(evaluation.getLawyer())) {
            throw new BackendException(ErrorStatus.CODE_ARGUMENT);
        }
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setVisible(true);
        return evaluationRepository.save(evaluation);
    }

    public Optional<Order> queryCustomerLastOrder(Long customerId) {
        return orderRepository.findFirstByCustomer_idOrderByCreateTimeDesc(customerId);
    }
}
