package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.customer.CustomerService;
import com.byakuya.boot.backend.component.dfb.lawyer.LawyerService;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.exception.BackendException;
import com.byakuya.boot.backend.exception.ErrorStatus;
import com.byakuya.boot.backend.exception.RecordNotFoundException;
import com.byakuya.boot.backend.utils.SnowFlakeUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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
    private final CommentRepository commentRepository;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, LawyerService lawyerService, CommentRepository commentRepository) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.lawyerService = lawyerService;
        this.commentRepository = commentRepository;
    }

    public Page<Comment> getCommentList(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public List<Comment> getVisibleComment(Pageable pageable) {
        return commentRepository.findAllByVisibleIsTrue(pageable).getContent();
    }

    public Object countVisibleLabel() {
        return commentRepository.countLabel();
    }

    @Transactional
    public Optional<Order> create(Long customerId, String excludeLawyer) {
        Customer customer = customerService.query(customerId, false).orElse(null);
        if (customer == null) {
            return Optional.empty();
        }
        Order old = orderRepository.findFirstByCustomer_idOrderByCreateTimeDesc(customerId).orElse(null);
        if (old != null) {
            switch (old.getState()) {
                case CREATED:
                case LAWYER_RESPONSE:
                case CALLING:
                    //存在进行中的订单
                    return Optional.of(old);
                case UN_PAY:
                    //存在进行中或未支付订单
                    throw AuthException.forbidden(null);
            }
        }
        return lawyerService.election(excludeLawyer).map(lawyer -> {
            lawyerService.beginWorking(lawyer);
            Order order = new Order();
            order.setCustomer(customer);
            order.setLawyer(lawyer);
            order.setCreateTime(LocalDateTime.now());
            order.setSerial(SnowFlakeUtils.newId());
            order.setState(OrderState.CREATED);
            order.setUpdateTime(order.getCreateTime());
            return orderRepository.save(order);
        });
    }

    @Transactional
    public Comment addComment(Comment comment, Long customerId) {
        if (comment.getOrderId() != null) {
            Order order = orderRepository.findById(comment.getOrderId()).orElseThrow(RecordNotFoundException::new);
            // 只能评价已支付未评价的订单并且只能订单的顾客本人评价
            if (order.getState() != OrderState.PAID || order.getCommentId() != null || !Objects.equals(order.getCustomer().getId(), customerId)) {
                throw AuthException.forbidden(null);
            }
            comment.setOrder(order);
            comment.setCustomer(order.getCustomer().getPhone());
            comment.setLawyer(order.getLawyer().getName());
            comment.setVisible(false);//真实评价默认不可见
        } else {
            comment.setOrder(null);
            comment.setVisible(true);//虚拟评价默认可见
        }
        if (!StringUtils.hasText(comment.getCustomer()) || !StringUtils.hasText(comment.getLawyer())) {
            throw new BackendException(ErrorStatus.CODE_ARGUMENT);
        }
        comment.setCreateTime(LocalDateTime.now());
        comment.setVisible(true);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment commentVisible(long commentId, boolean visible) {
        return commentRepository.save(commentRepository.findById(commentId).orElseThrow(RecordNotFoundException::new).setVisible(visible));
    }
}
