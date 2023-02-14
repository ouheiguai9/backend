package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.account.Account;
import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by 田伯光 at 2023/1/5 22:25
 */
@Data
@Entity
@Table(name = "T_DFB_ORDER")
public class Order implements Serializable {
    private static final long serialVersionUID = SystemVersion.SERIAL_VERSION_UID;

    @Id
    @GeneratedValue(generator = ConstantUtils.ID_GENERATOR_SEQUENCE_NAME)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @NotNull
    @Column(nullable = false, updatable = false, unique = true)
    private Long serial;
    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Lawyer lawyer;
    @JoinColumn(nullable = false, updatable = false)
    private LocalDateTime createTime;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long minutes;
    private BigDecimal fee;
    private String callRecord;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account updater;
    private LocalDateTime updateTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;
    @JsonIgnore
    @OneToOne(mappedBy = "order")
    private Evaluation evaluation;

    public Long getEvaluationId() {
        if (evaluation == null || evaluation.getId() == null) return null;
        return evaluation.getId();
    }

    public String getStateText() {
        return state == null ? null : state.text;
    }
}
