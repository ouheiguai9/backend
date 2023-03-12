package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.component.dfb.order.Comment;
import com.byakuya.boot.backend.component.dfb.order.OrderService;
import com.byakuya.boot.backend.config.ApiModule;
import com.byakuya.boot.backend.jackson.DynamicJsonView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by 田伯光 at 2023/3/1 21:29
 */
@ApiModule(path = "dfb", secure = false)
@Validated
class DongfabaoApi {
    private final OrderService orderService;

    DongfabaoApi(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/comment/list")
    @DynamicJsonView(type = Comment.class, include = {"secureCustomer", "secureLawyer", "createTime", "content", "value"})
    public List<Comment> getVisibleComment(@PageableDefault(sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getVisibleComment(pageable);
    }

    @GetMapping("/comment/label/stat")
    public Comment.LabelStat getLabelStat() {
        return orderService.visibleLabelStat();
    }
}
