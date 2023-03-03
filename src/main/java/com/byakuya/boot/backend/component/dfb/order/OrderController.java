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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AclApiModule(path = "dfb/orders", value = "dfb_order", desc = "订单管理")
@Validated
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @AclApiMethod(value = "comment_list", desc = "评价列表", path = "/comments", method = RequestMethod.GET)
    public Page<Comment> getCommentList(@PageableDefault(sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                        @RequestParam(required = false) Boolean visible,
                                        @RequestParam(value = "customer", required = false) String customerLike,
                                        @RequestParam(value = "lawyer", required = false) String lawyerLike,
                                        @RequestParam(value = "value", required = false) Integer[] valueIn,
                                        @RequestParam(value = "label", required = false) String[] labelIn,
                                        @RequestParam(value = "createTime", required = false) LocalDateTime[] createTimeIn) {
        return orderService.getCommentList(pageable, visible, customerLike, lawyerLike, valueIn, labelIn, createTimeIn);
    }

    @AclApiMethod(value = "comment_fake", desc = "虚拟评价", path = "/comments", method = RequestMethod.POST)
    public Comment fakeComment(@RequestBody Comment comment) {
        comment.setOrder(null);//虚拟评价不存在订单
        return orderService.addComment(comment, null);
    }

    @AclApiMethod(value = "comment_visible", desc = "评价显隐", path = "/comments/visible/{id}/{visible}", method = RequestMethod.POST)
    public Comment commentVisible(@PathVariable Long id, @PathVariable Boolean visible) {
        return orderService.commentVisible(id, visible);
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
