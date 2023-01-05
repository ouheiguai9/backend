package com.byakuya.boot.backend.component.dfb.order;

import com.byakuya.boot.backend.SystemVersion;
import com.byakuya.boot.backend.component.dfb.customer.Customer;
import com.byakuya.boot.backend.component.dfb.lawyer.Lawyer;
import com.byakuya.boot.backend.utils.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
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

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private Lawyer lawyer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime start;
    @Column(nullable = false, updatable = false)
    private LocalDateTime end;
}
