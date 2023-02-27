package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.config.AclApiMethod;
import com.byakuya.boot.backend.config.AclApiModule;
import com.byakuya.boot.backend.exception.AuthException;
import com.byakuya.boot.backend.security.AccountAuthentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@AclApiModule(path = "dfb/orders", value = "dfb_order", desc = "订单管理")
@Validated
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @AclApiMethod(value = "comment_list", desc = "评价列表", path = "/comments", method = RequestMethod.GET)
    public Page<Comment> getCommentList(@PageableDefault(sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getCommentList(pageable);
    }

    @AclApiMethod(value = "comment_fake", desc = "虚拟评价", path = "/comments", method = RequestMethod.POST)
    public Comment fakeComment(@RequestBody Comment comment, AccountAuthentication authentication) {
        comment.setOrder(null);//虚拟评价不存在订单
        return orderService.addComment(comment, authentication.getAccountId());
    }

    @PostMapping("/comment")
    public Comment realComment(@RequestBody Comment comment, AccountAuthentication authentication) {
        if (comment.getOrderId() == null) {
            //正常订单评价只能客户评价自己的订单
            throw AuthException.forbidden(null);
        }
        return orderService.addComment(comment, authentication.getAccountId());
    }
}
